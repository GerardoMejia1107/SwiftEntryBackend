---
tags: [swiftentry, entity, refund, wip]
---

# Reembolso

> [!summary]
> Un **Reembolso** representa dinero que se devuelve contra un [[Pago]]. ⚠️ **Esta es el área menos terminada de la app:** existen el modelo de datos, el enum, el repositorio y el mapper, y hay una *interfaz* de servicio — pero **no hay implementación de servicio ni controlador**. Trata esta página como una descripción del diseño previsto, no de un comportamiento que ya funcione.

Paquete: `domain/Refund/`

---

## 1. Modelo — `RefundModel` (tabla `refund`)

| Campo | Significado |
|---|---|
| `payment` → [[Pago]] | El pago que se reembolsa |
| `amount` | Cuánto devolver (`BigDecimal`) |
| `reason` | Por qué |
| `status` | `REQUESTED` / `APPROVED` / `REJECTED` / `COMPLETED` / `FAILED` |
| `refundedAt` | Cuándo el dinero realmente regresó |

---

## 2. Servicio previsto — `RefundService` (solo interfaz)

La interfaz esboza la API planeada. **Nada de esto está implementado todavía:**

- `createRefundRequest(req)`
- `getAllRefunds()` / `getRefundById(id)`
- `getRefundsByPaymentId(paymentId)` / `getRefundsByStatus(status)`
- `updateRefund(id, dto)`
- `approveRefund(id)` / `rejectRefund(id)`

---

## 3. Controlador
❌ **Ninguno.** Los reembolsos todavía no son alcanzables por HTTP.

## 4. Repositorio
`RefundRepository` — existe (`JpaRepository` estándar).

## 5. DTOs y Mapper
- `RefundRequestDTO`, `RefundUpdateDTO`, `RefundResponseDTO`, `RefundMapper` — todos presentes.

## 6. Relaciones
- Un reembolso pertenece a un [[Pago]].
- Completar un reembolso movería lógicamente la [[Reserva]] hacia `REFUNDED` y los boletos hacia `REFUNDED`/`CANCELLED` — pero **esa conexión todavía no existe**.

---

## 7. Notas y Detalles a Tener en Cuenta (estado: en construcción)
- 🔴 **No hay `RefundServiceImpl`** — la interfaz no tiene implementación.
- 🔴 **Sin controlador / endpoints.**
- 🟡 Los valores `REFUNDED` ya existen en `ReservationStatus` y `TicketStatus`, lo que sugiere el estado-final previsto, pero hoy nada transiciona hacia ellos.

> [!todo] Para que los reembolsos funcionen, el equipo necesitaría
> 1. Implementar `RefundServiceImpl` (crear solicitud → aprobar/rechazar → marcar `COMPLETED`).
> 2. Agregar un `RefundController`.
> 3. Propagar los cambios de estado a la [[Reserva]] / [[Boleto]] / [[Asiento|asientos]] relacionados.
> 4. Decidir si los asientos reembolsados regresan a `AVAILABLE` para revender.

## Ver También
- [[Pago]] — lo que un reembolso revierte
- [[Reserva]] · [[Boleto]]
