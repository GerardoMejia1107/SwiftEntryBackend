# Backend Verification Report
**Date:** 2026-06-28  
**Branch:** `feature/refunds`  
**Base URL:** http://localhost:8080/swift_entry  
**DB:** PostgreSQL `swift_entry_db` @ localhost:5432  
**Verified by:** Claude Code (`/backend-verifier` skill)

---

## Executive Summary

| Category | Count |
|----------|-------|
| ✅ Passed | 40 rules |
| ❌ Failed | 3 rules (2 high, 1 medium) |
| 🔧 Fixed in session | 1 (REF-BUG-01 — DB constraint) |
| ⚠️ Known gaps | 3 (pre-existing, documented) |

**Three bugs found; one fixed on-the-spot.** Two bugs require code changes: the event creation hardcodes venue name and zeroes locality capacity. One security bypass needs a route ordering fix.

---

## Flow: Authentication & Authorization

### AUTH-01 — Login with valid credentials returns JWT
**Rule:** `POST /auth/login` with correct email+password returns 200 and access + refresh tokens.  
**Status:** ✅ PASSED  
```bash
curl -s -X POST http://localhost:8080/swift_entry/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"testadmin@verify.com","password":"Verify123!"}'
# Response 200: {"data":{"accessToken":"eyJ...","refreshToken":"eyJ..."}, ...}
```

### AUTH-02 — Wrong password returns 401
**Status:** ✅ PASSED  
```bash
curl -s -o /dev/null -w "%{http_code}" -X POST http://localhost:8080/swift_entry/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"testuser@verify.com","password":"WrongPassword"}'
# Response: 401
```

### AUTH-03 — Protected endpoint without token returns 401
**Status:** ✅ PASSED  
```bash
curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/swift_entry/users
# Response: 401
```

### AUTH-04 — `GET /users` is admin-only
**Rule:** The `/users` collection endpoint must return 403 for non-admin authenticated users.  
**Status:** ❌ FAILED  
**Evidence:**
```bash
# Consumer token
curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/swift_entry/users \
  -H "Authorization: Bearer $USER_TOKEN"
# Response: 200  ← expected 403
```
**Finding:** `SecurityRoutes.AUTHENTICATED_GET_ENDPOINTS` contains `/swift_entry/users/**`. Spring Security evaluates rules in declaration order. The `users/**` pattern is matched before `ADMIN_GET_ENDPOINTS` (`/swift_entry/users`), granting any authenticated user access to the admin user list. The wildcard `**` appears to satisfy the `/users` path (empty suffix). Fix: remove `/swift_entry/users/**` from `AUTHENTICATED_GET_ENDPOINTS` and add `/swift_entry/users/{id}` explicitly; or reorder rules so the admin-only exact match is declared first.

### AUTH-05 — Refresh token rotates correctly
**Status:** ✅ PASSED — Refresh token returns a new access token; old refresh token is consumed.

### AUTH-06 — Logout invalidates refresh token
**Status:** ✅ PASSED — `POST /auth/logout` deletes the refresh token; reuse returns 401.

### AUTH-ADMIN — Admin endpoints block non-admin users
**Status:** ✅ PASSED  
- `GET /refunds` → consumer gets 403, admin gets 200  
- `GET /waiting-list` → consumer gets 403, admin gets 200  
- `POST /refunds/{id}/approve` → consumer gets 403, admin gets 200

---

## Flow: Events & Seat Map

### EVENT-01 — POST /events creates event
**Status:** ❌ FAILED (partial — event is created, but with wrong data)  
**Evidence:**
```bash
curl -s -X POST http://localhost:8080/swift_entry/events \
  -H "Content-Type: application/json" \
  -d '{"name":"Test Event","venueName":"Test Venue","category":"CONCERT","status":"PUBLISHED",...}'
# Response 201 but:
#   venueName = "Universidad Centroamericana José Simeón Cañas"  ← hardcoded
#   localities[0].capacity = 0   ← capacity input silently ignored
```
**Finding — BUG EVENT-01 (High):** `EventServiceImpl.createEvent()` ignores the request's `venueName` and hardcodes a fixed string. It also hardcodes `address.id = 30` instead of creating a new address from the request body. Root cause: `createEvent` was never fully implemented — hardcoded values were left in the code.

**Finding — BUG EVENT-02 (High):** `LocalityRequestDTO` has the `capacity` field commented out and `LocalityMapper.toModel(LocalityRequestDTO, ...)` hardcodes `capacity(0)` and `availableSlots(0)`. All localities created via `POST /events` have zero capacity. They cannot be used for reservations. Root cause: capacity field was commented out during refactoring and never restored.

### SEAT-01 — GET /seats/event/{id} returns seat map
**Status:** ✅ PASSED  
```bash
curl -s http://localhost:8080/swift_entry/seats/event/6 -H "Authorization: Bearer $TOKEN"
# Response 200: 160 seats, 3 AVAILABLE, 3 SOLD; includes localitySeatId, row, col, localityName, status
```

---

## Flow: Reservations

### RES-01 — Create reservation with early bird discount
**Rule:** Reservation subtracts early bird discount from seat price and applies 13% tax.  
**Status:** ✅ PASSED  
```bash
curl -s -X POST http://localhost:8080/swift_entry/reservations \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"localitySeatIds": [4, 5]}'
# Response 201: totalAmount=180.80, taxAmount=20.80, discountAmount=40.00
# Calculation: 2 × (100 × 0.80) = 160 subtotal, × 1.13 = 180.80 ✓
```
**DB verification:** seats 4 and 5 changed to `RESERVED`; `reservationExpiresAt` set to +15 minutes.

### RES-02 — Cannot reserve already RESERVED seat
**Status:** ✅ PASSED — Returns 409: `"Seat A4 is not available (status: RESERVED)."`

### RES-03 — GET /reservations/me
**Status:** ✅ PASSED — Returns own PENDING and CONFIRMED reservations.

### RES-04 — Global 5-seat cap per user per event
**Status:** ✅ PASSED  
```bash
# User holds 3 seats (PENDING), tries to add 3 more:
curl -s -X POST http://localhost:8080/swift_entry/reservations \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"localitySeatIds": [8, 9, 10]}'
# Response 409: "Cannot hold more than 5 tickets for the same event (you already hold 3)."

# Adding only 2 more (total=5) succeeds:
curl -s -X POST ... -d '{"localitySeatIds": [8, 9]}'
# Response 201 ✓
```

---

## Flow: Payments

### PAY-01 — Pay reservation → tickets issued
**Rule:** `POST /payments` on a PENDING reservation → payment APPROVED, reservation CONFIRMED, seats SOLD, one ISSUED ticket per seat.  
**Status:** ✅ PASSED  
```bash
curl -s -X POST http://localhost:8080/swift_entry/payments \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"reservationId": 15, "paymentMethod": "CREDIT_CARD"}'
# Response 201: status=APPROVED, amount=180.80, tickets=[{status:ISSUED},{status:ISSUED}]
```
**DB verification:** reservation→CONFIRMED; seats 4,5→SOLD; 2 tickets with unique TKT-* and QR-* codes→ISSUED.

### PAY-02 — Cannot pay already CONFIRMED reservation
**Status:** ✅ PASSED — Returns 409: `"Reservation is not payable in its current status: CONFIRMED."`

### PAY-03 — Cannot pay another user's reservation
**Status:** ✅ PASSED — Returns 403: `"This reservation does not belong to you."`

---

## Flow: Tickets

### TKT-01 — GET /tickets/me returns current holder's tickets
**Status:** ✅ PASSED — Returns ISSUED tickets including transferred ones (new holder sees theirs; old holder doesn't).

### TKT-02 — GET /tickets/{id}/qr returns QR image
**Status:** ✅ PASSED  
```bash
curl -s http://localhost:8080/swift_entry/tickets/4/qr -H "Authorization: Bearer $HOLDER_TOKEN"
# Response 200: Content-Type: image/png, size=419 bytes (ZXing-generated)
```

### TKT-02b — Non-holder cannot get QR
**Status:** ✅ PASSED — Returns 403.

### TKT-03a — Non-organizer cannot validate ticket
**Status:** ✅ PASSED — Returns 403: `"No está autorizado para realizar esta acción."`

### TKT-03b — Organizer validates ticket during active event
**Status:** ✅ PASSED  
```bash
curl -s -X POST http://localhost:8080/swift_entry/tickets/validate \
  -H "Authorization: Bearer $HOLLY_TOKEN" \
  -d '{"qrCode":"QR-6b7c7d7f-..."}'
# Response 200: status=USED, validatedAt=..., usedAt=...
```
**5-step chain verified:** TicketExistsHandler → TicketStatusHandler → ValidatorExistsHandler → EventOwnershipHandler → EventDateHandler all fire correctly.

### TKT-03c — Already USED ticket rejected
**Status:** ✅ PASSED — Returns 409: `"El ticket no puede ser validado por su estado: USED"`

### TKT-03d — Non-existent QR code returns 404
**Status:** ✅ PASSED — Returns 404: `"Ticket not found with QR code: QR-nonexistent-..."`

### TKT-03e — Validation blocked when event not live
**Status:** ✅ PASSED — Returns 409: `"El evento no ha empezado"` when `now < startDate`.

### TKT-04 — Ticket transfer to another user
**Status:** ✅ PASSED  
```bash
curl -s -X POST http://localhost:8080/swift_entry/tickets/5/transfer \
  -H "Authorization: Bearer $SENDER_TOKEN" \
  -d '{"receiverEmail":"otheruser@verify.com"}'
# Response 200: new TKT-* and QR-* codes generated; old codes invalidated
```
**DB verification:** `currentHolder_id` updated to receiver; `TicketTransferModel` audit record created.
- Old holder `GET /tickets/5/qr` → 403 ✅  
- New holder `GET /tickets/5/qr` → 200 ✅

---

## Flow: Refunds

### REF-01 — User requests refund for own APPROVED payment
**Status:** ✅ PASSED — Returns 201 with `status=REQUESTED`.

### REF-02 — Duplicate refund rejected
**Status:** ✅ PASSED — Returns 409: `"A pending refund already exists for this payment"`

### REF-03 — Non-owner cannot request refund
**Status:** ✅ PASSED — Returns 403: `"You are not the owner of this payment"`

### REF-04 — Refund amount cannot exceed payment amount
**Status:** ✅ PASSED — Returns 400: `"Refund amount cannot exceed the original payment amount of 180.80"`

### REF-05 — Non-admin cannot approve/reject refunds
**Status:** ✅ PASSED — Returns 403: `"Access denied: you do not have permission to perform this action"`

### REF-06 — Admin rejects refund → REJECTED
**Status:** ✅ PASSED — Returns 200 with `status=REJECTED`.

### REF-07 — Full refund approval cascades correctly
**Status:** ✅ PASSED (after fixing DB constraint — see REF-BUG-01)  
```bash
curl -s -X POST http://localhost:8080/swift_entry/refunds/2/approve \
  -H "Authorization: Bearer $ADMIN_TOKEN"
# Response 200: status=COMPLETED, refundedAt=...
```
**DB verification:**
- `refund.status` → `COMPLETED` ✅  
- `payment.status` → `REFUNDED` ✅  
- `reservation.status` → `REFUNDED` ✅  
- ISSUED tickets → `REFUNDED` ✅ (USED tickets unchanged — correct)  
- LocalitySeats → `AVAILABLE` ✅  
- `locality.availableSlots` restored by 2 ✅

### REF-07-BUG — Full refund approval returned 500 before fix
**Status:** 🔧 FIXED IN SESSION  
**Root cause:** `PaymentStatus.REFUNDED` was added to the Java enum (LOG-005) but PostgreSQL's `payment_status_check` constraint still listed only the original statuses (`PENDING`, `APPROVED`, `FAILED`, `CANCELLED`). Hibernate's `ddl-auto: update` does not regenerate CHECK constraints.  
**Fix applied:**
```sql
ALTER TABLE payment DROP CONSTRAINT payment_status_check;
ALTER TABLE payment ADD CONSTRAINT payment_status_check
  CHECK (status IN ('PENDING', 'APPROVED', 'FAILED', 'CANCELLED', 'REFUNDED'));
```
**Permanent fix needed:** Either migrate the constraint at deployment time, or remove the DB-level check and rely on the Java enum validation only.

### REF-08 — Admin lists all refunds
**Status:** ✅ PASSED — `GET /refunds` returns 200 with complete list.

### REF-09 — 48h window enforced
**Status:** ✅ PASSED  
```bash
# Event set to start in 24h, then refund attempted:
curl -s -X POST http://localhost:8080/swift_entry/refunds \
  -d '{"paymentId": 10, "amount": 90.40, "reason": "Late test"}'
# Response 400: "Refund window has closed — refunds must be requested at least 48 hours before the event"
```

### REF-10 — User views own payment refunds
**Status:** ✅ PASSED — `GET /refunds/payment/{paymentId}` returns 200 for the payment owner.

### REF-11 — Non-owner cannot view others' refunds
**Status:** ✅ PASSED — Returns 403: `"You are not the owner of this payment"`

---

## Flow: Waiting List

### WL-01 — Cannot join waiting list when seats are available
**Status:** ✅ PASSED — Returns 400: `"Locality has available seats. Please make a reservation directly."`

### WL-02 — Can join waiting list when locality is sold out
**Status:** ✅ PASSED  
```bash
# After all 9 locality seats are SOLD or RESERVED (availableSlots=0):
curl -s -X POST http://localhost:8080/swift_entry/waiting-list \
  -H "Authorization: Bearer $WL_TOKEN" \
  -d '{"localityId": 20}'
# Response 201: status=WAITING
```

### WL-03 — Duplicate waiting list entry rejected
**Status:** ✅ PASSED — Returns 409: `"You already have an active waiting list entry for this locality."`

### WL-04 — User views own waiting list entries
**Status:** ✅ PASSED — `GET /waiting-list/me` returns 200 with user's entries.

### WL-05 — Admin views all waiting list entries
**Status:** ✅ PASSED — `GET /waiting-list` returns 200 (admin token).

### WL-06 — Non-admin blocked from viewing all entries
**Status:** ✅ PASSED — Returns 403.

### WL-07 — Seat release triggers NOTIFIED status
**Rule:** When a reservation is cancelled and seats are released, WAITING users in the queue are promoted to NOTIFIED.  
**Status:** ✅ PASSED  
```bash
# Cancel reservation holding 3 seats (reservation 21)
curl -s -X DELETE http://localhost:8080/swift_entry/reservations/21 -H "Authorization: Bearer $TOKEN"
# Immediately after: GET /waiting-list/me returns entry with status=NOTIFIED
```

### WL-08 — User can cancel waiting list entry
**Status:** ✅ PASSED — `DELETE /waiting-list/{id}` returns 200 with `status=CANCELLED`.

---

## Flow: Notifications

### NOTIF-01 — Seat-available notification created
**Status:** ✅ PASSED — After WL-07, `GET /notifications/me` returns 1 notification of type `WAITING_LIST_SEAT_AVAILABLE`.

### NOTIF-02 — Unread count endpoint
**Status:** ✅ PASSED — `GET /notifications/me/unread-count` returns `{"unreadCount": 1}`.

### NOTIF-03 — Mark notification as read
**Status:** ✅ PASSED — `PATCH /notifications/{id}/read` returns 200 with `read=true`.

### NOTIF-04 — Mark all notifications as read
**Status:** ✅ PASSED — `PATCH /notifications/me/read-all` returns 200.

### NOTIF-05 — Cannot mark another user's notification as read
**Status:** ✅ PASSED — Returns 403: `"This notification does not belong to you."`

---

## Findings Summary

| ID | Severity | Rule | Status | Finding |
|----|----------|------|--------|---------|
| AUTH-04 | Medium | `GET /users` is admin-only | ❌ FAILED | `/swift_entry/users/**` in AUTHENTICATED_GET_ENDPOINTS matches the admin-only `/users` path; any logged-in user can list all users |
| EVENT-01 | High | Event creation uses provided venueName | ❌ FAILED | `createEvent()` hardcodes `"Universidad Centroamericana José Simeón Cañas"` and ignores request field |
| EVENT-02 | High | Locality capacity set from request | ❌ FAILED | `capacity` field commented out in `LocalityRequestDTO`; `LocalityMapper` hardcodes `capacity(0)` and `availableSlots(0)` |
| REF-BUG-01 | High | Full refund approve works | 🔧 FIXED | `payment_status_check` DB constraint missing `REFUNDED`; fixed by altering the constraint in this session |
| RES-MINOR | Low | Reservation response includes `expiresAt` | ⚠️ | `reservationExpiresAt` stored in DB but not included in response DTO |

---

## Reproduction Commands for Failed Tests

### AUTH-04
```bash
# Create a regular user and log in
TOKEN=$(curl -s -X POST http://localhost:8080/swift_entry/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"testuser@verify.com","password":"Verify123!"}' \
  | python3 -c "import sys,json; print(json.load(sys.stdin)['data']['accessToken'])")

# Consumer should get 403 but gets 200:
curl -s -o /dev/null -w "%{http_code}" \
  http://localhost:8080/swift_entry/users -H "Authorization: Bearer $TOKEN"
# Prints: 200  (expected: 403)
```

### EVENT-01 and EVENT-02
```bash
TOKEN=$(curl -s -X POST http://localhost:8080/swift_entry/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"testadmin@verify.com","password":"Verify123!"}' \
  | python3 -c "import sys,json; print(json.load(sys.stdin)['data']['accessToken'])")

curl -s -X POST http://localhost:8080/swift_entry/events \
  -H "Content-Type: application/json" \
  -d '{
    "name":"My Event","venueName":"My Venue","category":"CONCERT","status":"PUBLISHED",
    "organizerId":26,"startDate":"2026-12-15T20:00:00","endDate":"2026-12-15T23:00:00",
    "address":{"streetAddress":"Av. 1","municipality":"San Salvador","department":"San Salvador","country":"El Salvador"},
    "localities":[{"name":"VIP","price":200.00,"capacity":50}]
  }'
# Response: localities[0].capacity=0, venueName="Universidad Centroamericana José Simeón Cañas"
```

### REF-BUG-01 (now fixed, reproduction steps for dev environment)
```bash
# On a fresh DB where REFUNDED was never added to the constraint:
ADMIN_TOKEN=$(...)  # admin token
curl -s -X POST http://localhost:8080/swift_entry/refunds/1/approve \
  -H "Authorization: Bearer $ADMIN_TOKEN"
# Returns 500: DataIntegrityViolationException - payment_status_check constraint

# Fix:
psql -U swift_entry_manager_db -d swift_entry_db -c "
  ALTER TABLE payment DROP CONSTRAINT payment_status_check;
  ALTER TABLE payment ADD CONSTRAINT payment_status_check
    CHECK (status IN ('PENDING','APPROVED','FAILED','CANCELLED','REFUNDED'));
"
```

---

## Known Pre-Existing Gaps (not bugs, documented)

| ID | Description | Status |
|----|-------------|--------|
| B4 | `simulatePaymentProcessing()` always returns `true` — no real payment gateway | Known, documented |
| B5 | `emailVerified` field never checked or set via flow | Known, documented |
| B6 | JWT secret hardcoded in `application.yaml` | Known, documented |
| FEAT-14 | No occupancy/sales reporting endpoints | Known missing feature |
