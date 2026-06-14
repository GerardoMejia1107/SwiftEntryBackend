package com.gerardo.swiftentrybackend.domain.Event.services;

import com.gerardo.swiftentrybackend.domain.Address.model.AddressModel;
import com.gerardo.swiftentrybackend.domain.Address.repositories.AddressRepository;
import com.gerardo.swiftentrybackend.domain.Address.utils.AddressMapper;
import com.gerardo.swiftentrybackend.domain.Event.EventModel;
import com.gerardo.swiftentrybackend.domain.Event.dto.request.EventRequestDTO;
import com.gerardo.swiftentrybackend.domain.Event.dto.response.EventResponseDTO;
import com.gerardo.swiftentrybackend.domain.Event.repositories.EventRepository;
import com.gerardo.swiftentrybackend.domain.Event.utils.EventMapper;
import com.gerardo.swiftentrybackend.domain.Locality.LocalityModel;
import com.gerardo.swiftentrybackend.domain.Locality.repositories.LocalityRepository;
import com.gerardo.swiftentrybackend.domain.Locality.utils.LocalityMapper;
import com.gerardo.swiftentrybackend.domain.User.models.UserModel;
import com.gerardo.swiftentrybackend.domain.User.repositories.UserRepository;
import com.gerardo.swiftentrybackend.common.exceptions.ResourceConflictException;
import com.gerardo.swiftentrybackend.common.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;
    private final LocalityMapper localityMapper;
    private final LocalityRepository localityRepository;

    @Override
    public EventResponseDTO createEvent(EventRequestDTO eventRequestDTO) {
        UserModel organizer = userRepository.findById(eventRequestDTO.getOrganizerId())
                .orElseThrow();
        AddressModel eventAddress = AddressModel.builder()
                .streetAddress(eventRequestDTO.getAddress()
                        .getStreetAddress())
                .neighborhood(eventRequestDTO.getAddress()
                        .getNeighborhood())
                .municipality(eventRequestDTO.getAddress()
                        .getMunicipality())
                .department(eventRequestDTO.getAddress()
                        .getDepartment())
                .country(eventRequestDTO.getAddress()
                        .getCountry())
                .referencePoint(eventRequestDTO.getAddress()
                        .getReferencePoint())
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
                .venueName(eventRequestDTO.getVenueName())
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

    @Override
    public List<EventResponseDTO> getAllEvents() {
        return null;
    }

    @Override
    public EventResponseDTO getEventById(Integer id) {
        return null;
    }

    @Override
    public EventResponseDTO updateEvent(Integer id, EventRequestDTO request) {
        return null;
    }

    @Override
    public void deleteEvent(Integer id) {

    }
}
