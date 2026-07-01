# SwiftEntry Backend — Features & Role Audit

**Date:** 2026-06-14  
**Branch:** `develop`  
**Base URL:** `/swift_entry/`

---

## Table of Contents

1. [Role Overview](#1-role-overview)
2. [Authentication](#2-authentication-all-roles--public)
3. [Feature Matrix by Domain](#3-feature-matrix-by-domain)
4. [Endpoint Inventory with Role Access](#4-endpoint-inventory-with-role-access)
5. [Implementation Completeness](#5-implementation-completeness)
6. [Security Observations](#6-security-observations)
7. [Domains Not Yet Exposed via API](#7-domains-not-yet-exposed-via-api)
8. [Gap Summary](#8-gap-summary)

---

## 1. Role Overview

The application defines roles as plain database records (no code enum). Three roles are expected based on domain context and CLAUDE.md:

| Role | Spring Authority | Intended Purpose |
|---|---|---|
| `ADMINISTRATOR` | `ROLE_ADMINISTRATOR` | Platform management, full user and role catalog oversight |
| `ORGANIZER` | `ROLE_ORGANIZER` | Creates and manages events, localities, and seats |
| `CONSUMER` | `ROLE_CONSUMER` | Browses events, makes reservations, manages own tickets |

> **Important:** The security config only enforces a hard role gate for `ADMINISTRATOR`. All other authenticated routes apply to any logged-in user regardless of role. There is no ORGANIZER or CONSUMER enforcement at the framework level yet.

---

## 2. Authentication (All Roles / Public)

Base path: `/swift_entry/auth/`  
All endpoints are public (no token required).

| Method | Endpoint | Description |
|---|---|---|
| POST | `/auth/login` | Authenticate with email + password; returns JWT access token + refresh token |
| POST | `/auth/refresh` | Exchange a valid refresh token for a new access-token/refresh-token pair (token rotation) |
| POST | `/auth/logout` | Revoke a refresh token, invalidating the session |

**Notes:**
- JWT is HS256, 24-hour expiry.
- Refresh tokens are stored in the database and rotated on each use.
- The user's id, name, last name, email, and role are returned in the login response body.

---

## 3. Feature Matrix by Domain

Legend: ✅ Implemented & working · ⚠️ Partial / stubbed · ❌ Not implemented · 🔒 Role-gated

### 3.1 Users

| Feature | ADMINISTRATOR | ORGANIZER | CONSUMER | Public |
|---|---|---|---|---|
| Register account | — | — | — | ✅ |
| List all users | ✅ 🔒 | ❌ | ❌ | — |
| View own profile | ✅ | ✅ | ✅ | — |
| View any user profile | ✅ 🔒 (service) | ❌ | ❌ | — |
| Update user | ❌ | ❌ | ❌ | — |
| Delete user | ❌ | ❌ | ❌ | — |

### 3.2 Roles

| Feature | ADMINISTRATOR | ORGANIZER | CONSUMER | Public |
|---|---|---|---|---|
| Create role | ✅ ⚠️ (any auth) | ✅ ⚠️ (any auth) | ✅ ⚠️ (any auth) | — |
| List all roles | ✅ 🔒 | ❌ | ❌ | — |
| Update role | ❌ | ❌ | ❌ | — |
| Delete role | ❌ | ❌ | ❌ | — |

### 3.3 Events

| Feature | ADMINISTRATOR | ORGANIZER | CONSUMER | Public |
|---|---|---|---|---|
| Create event | ✅ | ✅ | ✅ | ✅ ⚠️ (no auth) |
| List all events | ✅ | ✅ | ✅ | ✅ |
| View event by ID | ✅ | ✅ | ✅ | — (auth required) |
| Update event | ✅ | ✅ | ✅ ⚠️ (any auth) | — |
| Delete event | ✅ | ✅ | ✅ ⚠️ (any auth) | — |
| Cancel event (status) | ✅ via update | ✅ via update | ✅ ⚠️ (any auth) | — |

**Event fields:** name, description, category (CONCERT / SPORTS / THEATER / COMEDY / CONFERENCE / FESTIVAL / MOVIE / CULTURAL), organizer, address, start/end dates, venue name, status (DRAFT / PUBLISHED / CANCELLED / FINISHED), image URL, localities list.

**Delete guard:** Deletion is blocked if any reservation exists for the event's seats. The API instructs the caller to set status to CANCELLED instead.

### 3.4 Localities

| Feature | ADMINISTRATOR | ORGANIZER | CONSUMER | Public |
|---|---|---|---|---|
| Create locality (standalone) | ❌ ⚠️ stubbed | — | — | ❌ ⚠️ stubbed |
| Create locality via event | ✅ | ✅ | ✅ | ✅ (via event create) |
| List all localities | ✅ | ✅ | ✅ | ✅ |
| Get locality by ID | ✅ | ✅ | ✅ | ✅ |
| Get localities by event | ✅ | ✅ | ✅ | ✅ |
| Update locality (standalone) | ✅ | ✅ | ✅ | ✅ ⚠️ (no auth) |
| Update locality via event | ✅ | ✅ | ✅ | ✅ (via event update) |
| Delete locality | ✅ | ✅ | ✅ | ✅ ⚠️ (no auth) |

**Locality fields:** name, description, price, capacity, available slots. Uses optimistic locking (`@Version`) to prevent race conditions during concurrent reservations.

> **Note:** `LocalityServiceImpl.createLocality()` is commented out and returns `null`. Localities can only be created via the event creation/update flows.

### 3.5 Seats

| Feature | ADMINISTRATOR | ORGANIZER | CONSUMER | Public |
|---|---|---|---|---|
| Create seats (bulk) | ✅ | ✅ | ✅ | ✅ ⚠️ (no auth) |
| List all seats | — | — | — | ⚠️ (returns empty list) |
| Get seat by ID | ✅ | ✅ | ✅ | ✅ |
| Get seats by locality | ✅ | ✅ | ✅ | ✅ |
| Update seat | ❌ (commented out) | ❌ | ❌ | — |
| Delete seat | ✅ | ✅ | ✅ | ✅ ⚠️ (no auth) |

**Bulk creation:** Generates seats by providing row labels + seats-per-row count. Each seat gets status `AVAILABLE`. Updates locality capacity automatically.  
**Seat statuses:** `AVAILABLE` / `RESERVED` / `SOLD` / `BLOCKED` / `DISABLED`.

### 3.6 Reservations

| Feature | ADMINISTRATOR | ORGANIZER | CONSUMER | Public |
|---|---|---|---|---|
| Create reservation | ✅ ⚠️ partial | ✅ ⚠️ partial | ✅ ⚠️ partial | — |
| List all reservations | ❌ stubbed | ❌ stubbed | ❌ stubbed | — |
| Get reservation by ID | ❌ stubbed | ❌ stubbed | ❌ stubbed | — |
| Get reservations by user | ❌ stubbed | ❌ stubbed | ❌ stubbed | — |
| Get reservations by status | ❌ stubbed | ❌ stubbed | ❌ stubbed | — |
| Update reservation | ❌ stubbed | ❌ stubbed | ❌ stubbed | — |
| Cancel reservation | ❌ stubbed | ❌ stubbed | ❌ stubbed | — |
| Expire pending reservations | ❌ stubbed | ❌ stubbed | ❌ stubbed | — |

> **No HTTP controller exists for Reservation.** The service implementation exists but is largely stubbed. `createReservation` has business logic but does not persist the `ReservationSeat` join records (it builds the model but never calls `reservationSeatRepository.save()`).

**Pricing logic (implemented in service):**
- Subtotal = sum of locality prices for selected seats
- Tax = 13% of subtotal
- Discount = $0.00 (hardcoded)
- Total = subtotal + tax − discount
- Reservation expires 15 minutes after creation

**Reservation statuses:** `PENDING` / `CONFIRMED` / `EXPIRED` / `CANCELLED` / `REFUNDED`.

---

## 4. Endpoint Inventory with Role Access

Full table of every active HTTP endpoint, its actual access level (as enforced by Spring Security), and whether service-layer logic adds further restrictions.

| Method | Path | Security Layer | Service-Level Check | Notes |
|---|---|---|---|---|
| POST | `/auth/login` | Public | — | |
| POST | `/auth/refresh` | Public | — | |
| POST | `/auth/logout` | Public | Requires valid refresh token | |
| POST | `/users` | Public | Email uniqueness check | |
| GET | `/users` | ADMINISTRATOR only | — | |
| GET | `/users/{id}` | Authenticated | Admin OR same user | |
| POST | `/roles` | Authenticated (any role) | — | ⚠️ Should be ADMIN-only |
| GET | `/roles` | ADMINISTRATOR only | — | |
| POST | `/events` | Public | — | ⚠️ Should require auth |
| GET | `/events` | Public | — | |
| GET | `/events/{id}` | Authenticated (any role) | — | ⚠️ Inconsistent with GET /events |
| PUT | `/events/{id}` | Authenticated (any role) | — | ⚠️ No ownership check |
| DELETE | `/events/{id}` | Authenticated (any role) | Blocks if reservations exist | ⚠️ No ownership check |
| POST | `/localities` | Public | Returns null (stubbed) | ⚠️ Should require auth |
| GET | `/localities` | Public | — | |
| GET | `/localities/{id}` | Public | — | |
| GET | `/localities/event/{eventId}` | Public | — | |
| PUT | `/localities/{id}` | Public | — | ⚠️ Security hole |
| DELETE | `/localities/{id}` | Public | — | ⚠️ Security hole |
| POST | `/seats/bulk` | Public | — | ⚠️ Security hole |
| GET | `/seats` | Public | Returns empty list (stub) | |
| GET | `/seats/{id}` | Public | — | |
| GET | `/seats/locality/{localityId}` | Public | — | |
| DELETE | `/seats/{id}` | Public | — | ⚠️ Security hole |

---

## 5. Implementation Completeness

| Domain | Model | DTOs | Repository | Service Interface | Service Impl | Controller | Status |
|---|---|---|---|---|---|---|---|
| User | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | Full (no update/delete) |
| Role | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | Full (no update/delete) |
| Address | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | Full (managed via User/Event) |
| Event | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | Full CRUD |
| Locality | ✅ | ✅ | ✅ | ✅ | ⚠️ partial | ✅ | Create stubbed; rest works |
| Seat | ✅ | ✅ | ✅ | ✅ | ⚠️ partial | ✅ | getAllSeats stubbed; update disabled |
| Reservation | ✅ | ✅ | ✅ | ✅ | ⚠️ partial | ❌ | Service mostly stubbed; no HTTP layer |
| ReservationSeat | ✅ | ✅ | ✅ | ✅ | ⚠️ inline | ❌ | Used inside Reservation service |
| Purchase | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ | Model + DTOs only |
| Payment | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ | Model + DTOs only |
| Ticket | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ | Model + DTOs only |
| Refund | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ | Model + DTOs only |

---

## 6. Security Observations

These are not bugs per se but gaps between intended design and current enforcement.

### 6.1 Missing Role Enforcement

The security config only gates on `ADMINISTRATOR`. No endpoint currently enforces `ORGANIZER` or `CONSUMER` as distinct access levels. Any authenticated user can:
- Create, update, or delete any event (regardless of ownership)
- Delete any seat
- Create a role

### 6.2 Unintentionally Public Mutations

The `PUBLIC_ENDPOINTS` array in `SecurityRoutes` is matched **without an HTTP method**, meaning Spring Security allows **any** HTTP verb to those paths without authentication:

```java
// SecurityRoutes.java
public static final String[] PUBLIC_ENDPOINTS = {
    "/swift_entry/auth/**", "/swift_entry/events",
    "/swift_entry/localities", "/swift_entry/localities/**",
    "/swift_entry/seats", "/swift_entry/seats/**"
};
```

This makes the following mutation endpoints unauthenticated:
- PUT `/localities/{id}` — update any locality
- DELETE `/localities/{id}` — delete any locality
- POST `/seats/bulk` — create seats for any event
- DELETE `/seats/{id}` — delete any seat
- POST `/events` — create an event (may be intentional)

### 6.3 Redundant/Conflicting Security Rule

`/swift_entry/events` appears in both `PUBLIC_ENDPOINTS` (permits all) and `ADMIN_GET_ENDPOINTS` (admin GET only). Because Spring Security evaluates rules in order, the public rule wins. GET `/swift_entry/events` is effectively public for all, not admin-gated — the admin rule for this URL is dead code.

### 6.4 Inconsistent Event Access

- `GET /events` — **public** (anyone can list events)
- `GET /events/{id}` — **authenticated** (requires login to view a single event)

This is inconsistent. A public event listing implies the detail view should also be public.

### 6.5 No Ownership Checks on Events

Any authenticated user can update or delete any event. There is no check confirming the caller is the event's organizer or an administrator.

---

## 7. Domains Not Yet Exposed via API

The following domains have full JPA models and DTOs but no service implementations or HTTP controllers:

| Domain | Key Enums / Statuses | Intended Role |
|---|---|---|
| **Purchase** | — | Records a finalized purchase after reservation is confirmed |
| **Payment** | Methods: CASH, CREDIT_CARD, DEBIT_CARD, BANK_TRANSFER · Statuses: (in model) | Records payment for a purchase |
| **Ticket** | Statuses: ISSUED, USED, CANCELLED, REFUNDED | Issued after successful payment; used for event entry validation |
| **Refund** | Statuses: REQUESTED, APPROVED, REJECTED, COMPLETED, FAILED | Manages refund lifecycle against a purchase or ticket |

---

## 8. Gap Summary

### What Works End-to-End Today

- User registration and JWT-based login/logout/refresh
- Full event CRUD (no role enforcement beyond authentication)
- Listing/viewing localities and seats (public read)
- Bulk seat creation for a locality
- Partial reservation creation (price calculation and seat status update work; join table persistence is incomplete)

### What Needs to Be Built

| Priority | Gap |
|---|---|
| High | Add HTTP controller + complete service for **Reservation** |
| High | Fix `ReservationServiceImp.createReservation` — `ReservationSeat` records are never persisted |
| High | Enforce **ownership on events** (only organizer or admin can update/delete) |
| High | Restrict seat/locality mutations to authenticated users (fix `PUBLIC_ENDPOINTS` to use method-specific matchers) |
| Medium | Implement **Purchase → Payment → Ticket** flow |
| Medium | Implement **Refund** flow |
| Medium | Add ORGANIZER and CONSUMER role gates where appropriate |
| Medium | Add `GET /events/{id}` to public endpoints for consistency |
| Medium | Restrict `POST /roles` to ADMINISTRATOR |
| Low | Fix the dead admin rule for `GET /events` in `SecurityRoutes` |
| Low | Implement `LocalityServiceImpl.createLocality` (currently returns null) |
| Low | Implement `SeatServiceImpl.getAllSeats` (currently returns empty list) |
| Low | Add update/delete for User and Role entities |
| Low | Re-enable `PUT /seats/{id}` (currently commented out) |
