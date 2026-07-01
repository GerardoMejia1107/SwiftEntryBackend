package com.gerardo.swiftentrybackend.domain.WaitingList.enums;

// Estados posibles de una entrada en la lista de espera de una localidad
public enum WaitingListStatus {
    // Esperando a que se libere un cupo
    WAITING,
    // Se le avisó que hay un cupo disponible y tiene una ventana de tiempo para reservar
    NOTIFIED,
    // El usuario completó la reserva dentro de la ventana de notificación
    FULFILLED,
    // El usuario abandonó voluntariamente la lista de espera
    CANCELLED,
    // La ventana de notificación venció sin que el usuario reservara
    EXPIRED
}
