package com.gerardo.swiftentrybackend.domain.WaitingList.service;

import com.gerardo.swiftentrybackend.domain.WaitingList.dto.request.WaitingListRequestDTO;
import com.gerardo.swiftentrybackend.domain.WaitingList.dto.response.WaitingListResponseDTO;

import java.util.List;

// Operaciones de gestión de la lista de espera de localidades sin cupo disponible
public interface WaitingListService {

    // Registra al usuario en la lista de espera de una localidad; falla si la localidad tiene cupo o ya está anotado
    WaitingListResponseDTO joinWaitingList(WaitingListRequestDTO dto, String userEmail);

    // Cancela una entrada WAITING o NOTIFIED perteneciente al usuario
    WaitingListResponseDTO leaveWaitingList(Integer id, String userEmail);

    List<WaitingListResponseDTO> getMyEntries(String userEmail);

    List<WaitingListResponseDTO> getAllEntries();

    List<WaitingListResponseDTO> getEntriesByLocality(Long localityId);

    // Notifica (pasa a NOTIFIED con ventana de expiración) a los siguientes usuarios en cola cuando se liberan cupos
    void notifyNextInQueue(Long localityId, int slotsReleased);

    // Marca como FULFILLED la entrada NOTIFIED del usuario cuando completa su reserva
    void fulfillNotifiedEntry(Integer userId, Long localityId);

    // Expira las entradas NOTIFIED vencidas y reencola al siguiente en turno; devuelve cuántas expiraron
    int expireNotifiedEntries();
}
