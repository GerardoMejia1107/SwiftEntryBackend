package com.gerardo.swiftentrybackend.domain.Event.services;

import com.gerardo.swiftentrybackend.domain.Address.model.AddressModel;
import com.gerardo.swiftentrybackend.domain.Address.repositories.AddressRepository;
import com.gerardo.swiftentrybackend.domain.Address.utils.AddressMapper;
import com.gerardo.swiftentrybackend.domain.Event.EventModel;
import com.gerardo.swiftentrybackend.domain.Event.dto.request.EventRequestDTO;
import com.gerardo.swiftentrybackend.domain.Event.dto.response.EventResponseDTO;
import com.gerardo.swiftentrybackend.domain.Event.repositories.EventRepository;
import com.gerardo.swiftentrybackend.domain.Event.utils.EventMapper;
import com.gerardo.swiftentrybackend.domain.User.models.UserModel;
import com.gerardo.swiftentrybackend.domain.User.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.w3c.dom.events.EventException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;

    @Override
    public EventResponseDTO createEvent(EventRequestDTO eventRequestDTO) {
        if (eventRepository.existsByName(eventRequestDTO.getName())) {
            throw new RuntimeException("Event already exists");
        }
        UserModel userModel = userRepository.findById(eventRequestDTO.getOrganizerId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        AddressModel addressModel = null;

        if (eventRequestDTO.getAddress() != null) {
            addressModel = addressRepository.save(addressMapper.toModel(eventRequestDTO.getAddress()));
        }

        EventModel eventModel = eventMapper.toModel(eventRequestDTO, userModel, addressModel);

        return eventMapper.toResponse(eventRepository.save(eventModel));
    }

    @Override
    public List<EventResponseDTO> getAllEvents() {
        return eventRepository.findAll()
                .stream()
                .map(eventMapper::toResponse)
                .toList();
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
