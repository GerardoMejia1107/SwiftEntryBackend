package com.gerardo.swiftentrybackend.domain.Event.services;

import com.gerardo.swiftentrybackend.domain.Address.model.AddressModel;
import com.gerardo.swiftentrybackend.domain.Address.repositories.AddressRepository;
import com.gerardo.swiftentrybackend.domain.Address.utils.AddressMapper;
import com.gerardo.swiftentrybackend.domain.Event.EventModel;
import com.gerardo.swiftentrybackend.domain.Event.dto.request.EventRequestDTO;
import com.gerardo.swiftentrybackend.domain.Event.dto.request.EventUpdateDTO;
import com.gerardo.swiftentrybackend.domain.Event.dto.response.EventResponseDTO;
import com.gerardo.swiftentrybackend.domain.Event.repositories.EventRepository;
import com.gerardo.swiftentrybackend.domain.Event.utils.EventMapper;
import com.gerardo.swiftentrybackend.domain.Locality.LocalityModel;
import com.gerardo.swiftentrybackend.domain.Locality.dto.request.LocalityUpdateDTO;
import com.gerardo.swiftentrybackend.domain.Locality.repositories.LocalityRepository;
import com.gerardo.swiftentrybackend.domain.Locality.utils.LocalityMapper;
import com.gerardo.swiftentrybackend.domain.Reservation.repositories.ReservationSeatRepository;
import com.gerardo.swiftentrybackend.domain.Seat.repositories.LocalitySeatRepository;
import com.gerardo.swiftentrybackend.domain.User.models.UserModel;
import com.gerardo.swiftentrybackend.domain.User.repositories.UserRepository;
import com.gerardo.swiftentrybackend.common.exceptions.ForbiddenOperationException;
import com.gerardo.swiftentrybackend.common.exceptions.ResourceConflictException;
import com.gerardo.swiftentrybackend.common.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
// Implementación de EventService.
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;
    private final LocalityMapper localityMapper;
    private final LocalityRepository localityRepository;
    private final LocalitySeatRepository localitySeatRepository;
    private final ReservationSeatRepository reservationSeatRepository;

    // Crea el evento y guarda cada una de sus localidades asociadas (capacity/availableSlots inician en 0).
    @Override
    public EventResponseDTO createEvent(EventRequestDTO eventRequestDTO) {
        UserModel organizer = userRepository.findById(eventRequestDTO.getOrganizerId())
                .orElseThrow();
        AddressModel eventAddress = AddressModel.builder()
                .id(30)
                .build();
        AddressModel savedAddress = addressRepository.save(eventAddress);
        EventModel event = EventModel.builder()
                .name(eventRequestDTO.getName())
                .description(eventRequestDTO.getDescription())
                .category(eventRequestDTO.getCategory())
                .organizer(organizer)
                .address(savedAddress)
                .startDate(eventRequestDTO.getStartDate())
                .endDate(eventRequestDTO.getEndDate())
                .venueName("Universidad Centroamericana José Simeón Cañas")
                .status(eventRequestDTO.getStatus())
                .imageUrl(eventRequestDTO.getImageUrl())
                .build();
        EventModel savedEvent = eventRepository.save(event);
        List<LocalityModel> savedLocalities = new ArrayList<>();

        eventRequestDTO.getLocalities()
                .forEach(localityRequestDTO -> {
                    LocalityModel newLocality = localityMapper.toModel(localityRequestDTO, savedEvent);
                    savedLocalities.add(localityRepository.save(newLocality));
                });

        return eventMapper.toResponse(savedEvent, savedLocalities);
    }

    // Lista todos los eventos junto con sus localidades.
    @Override
    public List<EventResponseDTO> getAllEvents() {
        return eventRepository.findAll()
                .stream()
                .map(event -> {
                    List<LocalityModel> localities = localityRepository.findAllByEvent_Id(event.getId());
                    return eventMapper.toResponse(event, localities);
                })
                .toList();
    }

    // Lista los eventos de un organizador; valida primero que el usuario exista.
    @Override
    public List<EventResponseDTO> getEventsByOrganizerId(Integer userId) {
        userRepository.findById(userId)
                .orElseThrow();

        return eventRepository.findAllByOrganizer_Id(userId)
                .stream()
                .map(event -> {
                    List<LocalityModel> localities = localityRepository.findAllByEvent_Id(event.getId());
                    return eventMapper.toResponse(event, localities);
                })
                .toList();
    }

    // Obtiene el evento y sus localidades; lanza ResourceNotFoundException si no existe.
    @Override
    public EventResponseDTO getEventById(Integer id) {
        EventModel event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));
        List<LocalityModel> localities = localityRepository.findAllByEvent_Id(id);
        return eventMapper.toResponse(event, localities);
    }

    // Actualiza campos no nulos del evento; si se envían localidades, delega la sincronización a processLocalityUpdates.
    @Override
    @Transactional
    public EventResponseDTO updateEvent(Integer id, EventUpdateDTO request) {
        EventModel event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));

        if (request.getName() != null) event.setName(request.getName());
        if (request.getDescription() != null) event.setDescription(request.getDescription());
        if (request.getCategory() != null) event.setCategory(request.getCategory());
        if (request.getStatus() != null) event.setStatus(request.getStatus());
        if (request.getStartDate() != null) event.setStartDate(request.getStartDate());
        if (request.getEndDate() != null) event.setEndDate(request.getEndDate());
        if (request.getVenueName() != null) event.setVenueName(request.getVenueName());
        if (request.getImageUrl() != null) event.setImageUrl(request.getImageUrl());
        if (request.getOrganizerId() != null) {
            UserModel organizer = userRepository.findById(request.getOrganizerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Organizer not found with id: " + request.getOrganizerId()));
            event.setOrganizer(organizer);
        }

        EventModel savedEvent = eventRepository.save(event);

        List<LocalityModel> finalLocalities;
        if (request.getLocalities() != null) {
            finalLocalities = processLocalityUpdates(savedEvent, request.getLocalities());
        } else {
            finalLocalities = localityRepository.findAllByEvent_Id(id);
        }

        return eventMapper.toResponse(savedEvent, finalLocalities);
    }

    // Elimina el evento con sus localidades y LocalitySeat; rechaza el borrado si ya existen reservas.
    @Override
    @Transactional
    public void deleteEvent(Integer id) {
        if (reservationSeatRepository.existsByLocalitySeat_Locality_Event_Id(id)) {
            throw new ForbiddenOperationException(
                    "Cannot delete event with id " + id + " because it has existing reservations. " +
                            "Cancel the event instead by setting its status to CANCELLED."
            );
        }

        List<LocalityModel> localities = localityRepository.findAllByEvent_Id(id);
        for (LocalityModel locality : localities) {
            localitySeatRepository.deleteAllByLocality_Id(locality.getId());
        }
        localityRepository.deleteAll(localities);
        eventRepository.deleteById(id);
    }

    // Sincroniza las localidades de un evento: elimina las que ya no vienen en el request, actualiza las existentes y crea las nuevas.
    private List<LocalityModel> processLocalityUpdates(EventModel event, List<LocalityUpdateDTO> requestLocalities) {
        List<LocalityModel> existingLocalities = localityRepository.findAllByEvent_Id(event.getId());

        Set<Long> incomingIds = requestLocalities.stream()
                .map(LocalityUpdateDTO::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        for (LocalityModel existing : existingLocalities) {
            if (!incomingIds.contains(existing.getId())) {
                if (reservationSeatRepository.existsByLocalitySeat_Locality_Id(existing.getId())) {
                    throw new ForbiddenOperationException(
                            "Cannot remove locality '" + existing.getName() + "' because it has existing reservations.");
                }
                localitySeatRepository.deleteAllByLocality_Id(existing.getId());
                localityRepository.delete(existing);
            }
        }

        for (LocalityUpdateDTO dto : requestLocalities) {
            if (dto.getId() != null) {
                LocalityModel existing = existingLocalities.stream()
                        .filter(l -> l.getId()
                                .equals(dto.getId()))
                        .findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Locality not found with id: " + dto.getId()));
                localityMapper.updateModel(existing, dto);
                localityRepository.save(existing);
            } else {
                if (dto.getName() == null || dto.getPrice() == null) {
                    throw new ResourceConflictException("New localities require at least 'name' and 'price'.");
                }
                localityRepository.save(localityMapper.toModel(dto, event));
            }
        }

        return localityRepository.findAllByEvent_Id(event.getId());
    }
}
