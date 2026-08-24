package com.mendes.check_in_hub.event;

import com.mendes.check_in_hub.event.DTO.EventRequest;
import com.mendes.check_in_hub.event.DTO.EventResponse;
import com.mendes.check_in_hub.exception.BusinessRuleException;
import com.mendes.check_in_hub.exception.EventNotFoundException;
import com.mendes.check_in_hub.exception.UnauthorizedOperationException;
import com.mendes.check_in_hub.exception.UserNotFoundException;
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
    public EventResponse createEvent (EventRequest request, User organizer) {

        if (organizer.getRole() != UserRole.ORGANIZER) {
            throw new BusinessRuleException("Only organizers can create events");
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
                .orElseThrow(() -> new EventNotFoundException(eventId));

        if (event.getStatus() != EventStatus.PUBLISHED) {
            throw new EventNotFoundException(eventId);
        }

        return EventResponse.fromEntity(event);
    }

    @Transactional
    public List<EventResponse> findAllEvents () {
        return eventRepository.findByStatus(EventStatus.PUBLISHED)
                .stream()
                .map(EventResponse::fromEntity)
                .toList();
    }

    @Transactional
    public void publishEvent (Long eventId, User organizer) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));

        if (!event.getOrganizer().getId().equals(organizer.getId())) {
            throw new UnauthorizedOperationException("User is not the organizer of this event");
        }

        if (event.getStatus() == EventStatus.DRAFT) {
            event.setStatus(EventStatus.PUBLISHED);
            eventRepository.save(event);
        }
    }

    @Transactional
    public void cancelEvent (Long eventId, User organizer) {
        Event event =  eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));

        if (!event.getOrganizer().getId().equals(organizer.getId())) {
            throw new UnauthorizedOperationException("User is not the organizer of this event");
        }

        event.setStatus(EventStatus.CANCELLED);
        eventRepository.save(event);
    }

    @Transactional
    public List<EventResponse> findOrganizerEvents (Long organizerId) {
        User user = userRepository.findById(organizerId)
                .orElseThrow(() -> new UserNotFoundException(organizerId));

        return eventRepository.findByOrganizerId(organizerId)
                .stream()
                .map(EventResponse::fromEntity)
                .toList();
    }

}
