package com.mendes.check_in_hub.event;

import com.mendes.check_in_hub.event.DTO.EventRequest;
import com.mendes.check_in_hub.event.DTO.EventResponse;
import com.mendes.check_in_hub.user.User;
import com.mendes.check_in_hub.user.UserRepository;
import com.mendes.check_in_hub.user.UserRole;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Transactional
    public EventResponse createEvent (EventRequest request) {
        User organizer = userRepository.findById(request.organizerId())
                .orElseThrow(() -> new IllegalArgumentException("Organizer not found with id: " + request.organizerId()));
        if (organizer.getRole() != UserRole.ORGANIZER) {
            throw new IllegalArgumentException("Organizer not found");
        }

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

    @Transactional
    public EventResponse findByEventId (Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + eventId));

        return EventResponse.fromEntity(event);
    }

    @Transactional
    public List<EventResponse> findAllEvents () {
        return eventRepository.findAll()
                .stream()
                .map(EventResponse::fromEntity)
                .toList();
    }

    @Transactional
    public void publishEvent (Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found with id: " + eventId));

        if (event.getStatus() == EventStatus.DRAFT) {
            event.setStatus(EventStatus.PUBLISHED);
            eventRepository.save(event);
        }
    }

    @Transactional
    public void cancelEvent (Long eventId) {
        Event event =  eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found with id: " + eventId));

        event.setStatus(EventStatus.CANCELLED);
        eventRepository.save(event);
    }

    @Transactional
    public List<EventResponse> findOrganizerEvents (Long organizerId) {
        User user = userRepository.findById(organizerId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + organizerId));

        return eventRepository.findByOrganizerId(organizerId)
                .stream()
                .map(EventResponse::fromEntity)
                .toList();
    }

}
