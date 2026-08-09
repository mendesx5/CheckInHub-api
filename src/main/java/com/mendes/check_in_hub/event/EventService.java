package com.mendes.check_in_hub.event;

import com.mendes.check_in_hub.event.DTO.EventRequest;
import com.mendes.check_in_hub.event.DTO.EventResponse;
import com.mendes.check_in_hub.user.User;
import com.mendes.check_in_hub.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Transactional
    public EventResponse createEvent (EventRequest request) {
        User organizer = userRepository.findById(request.organizerId())
                .orElseThrow(() -> new IllegalArgumentException("Organizer not found with id: " + request.organizerId()));

        Event event = Event.builder()
                .title(request.title())
                .description(request.description())
                .capacity(request.capacity())
                .dateTime(request.dateTime())
                .location(request.location())
                .status(EventStatus.DRAFT)
                .organizer(organizer)
                .build();

        Event savedEvent = eventRepository.save(event);

        return EventResponse.fromEntity(savedEvent);
    }

}
