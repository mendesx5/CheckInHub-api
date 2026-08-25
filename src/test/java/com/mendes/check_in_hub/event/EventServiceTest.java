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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EventService eventService;

    private User organizer(Long id) {
        return User.builder()
                .id(id)
                .name("Organizer")
                .email("organizer" + id + "@test.com")
                .role(UserRole.ORGANIZER)
                .build();
    }

    private Event publishedEvent(Long id, User organizer) {
        return Event.builder()
                .id(id)
                .title("Java Event")
                .description("Test event")
                .dateTime(LocalDateTime.now().plusDays(5))
                .location("Natal/RN")
                .capacity(100)
                .status(EventStatus.PUBLISHED)
                .organizer(organizer)
                .build();
    }

    @Test
    void participantShouldNotCreateEvent() {
        User participant = User.builder()
                .id(1L)
                .role(UserRole.PARTICIPANT)
                .build();

        EventRequest request = new EventRequest(
                "Test event",
                "Description",
                LocalDateTime.now().plusDays(5),
                "Natal/RN",
                100
        );

        assertThrows(
                BusinessRuleException.class,
                () -> eventService.createEvent(request, participant)
        );

        verify(eventRepository, never()).save(any());
    }

    @Test
    void organizerShouldCreateEvent() {
        User organizer = organizer(1L);

        EventRequest request = new EventRequest(
                "Java Event",
                "Event for developers",
                LocalDateTime.now().plusDays(10),
                "Natal/RN",
                50
        );

        when(eventRepository.save(any(Event.class)))
                .thenAnswer(invocation -> {
                    Event event = invocation.getArgument(0);
                    event.setId(1L);
                    return event;
                });

        EventResponse response = eventService.createEvent(request, organizer);

        assertNotNull(response);
        assertEquals("Java Event", response.title());
        assertEquals(EventStatus.DRAFT, response.status());

        verify(eventRepository).save(any(Event.class));
    }

    @Test
    void shouldFindPublishedEventById() {
        User organizer = organizer(1L);
        Event event = publishedEvent(10L, organizer);

        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));

        EventResponse response = eventService.findByEventId(10L);

        assertEquals(10L, response.id());
        assertEquals(EventStatus.PUBLISHED, response.status());
    }

    @Test
    void shouldThrowWhenEventDoesNotExist() {
        when(eventRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                EventNotFoundException.class,
                () -> eventService.findByEventId(99L)
        );
    }

    @Test
    void shouldNotExposeDraftEventById() {
        Event event = publishedEvent(10L, organizer(1L));
        event.setStatus(EventStatus.DRAFT);

        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));

        assertThrows(
                EventNotFoundException.class,
                () -> eventService.findByEventId(10L)
        );
    }

    @Test
    void shouldReturnPublishedEvents() {
        Event event1 = publishedEvent(1L, organizer(1L));
        Event event2 = publishedEvent(2L, organizer(2L));

        when(eventRepository.findByStatus(EventStatus.PUBLISHED)).thenReturn(List.of(event1, event2));

        List<EventResponse> response = eventService.findAllEvents();

        assertEquals(2, response.size());

        verify(eventRepository).findByStatus(EventStatus.PUBLISHED);
    }

    @Test
    void ownerShouldPublishEvent() {
        User organizer = organizer(1L);

        Event event = publishedEvent(10L, organizer);
        event.setStatus(EventStatus.DRAFT);

        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));

        eventService.publishEvent(10L, organizer);

        assertEquals(EventStatus.PUBLISHED, event.getStatus());

        verify(eventRepository).save(event);
    }

    @Test
    void nonOwnerShouldNotPublishEvent() {
        User owner = organizer(1L);
        User anotherOrganizer = organizer(2L);

        Event event = publishedEvent(10L, owner);
        event.setStatus(EventStatus.DRAFT);

        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));

        assertThrows(
                UnauthorizedOperationException.class,
                () -> eventService.publishEvent(
                        10L,
                        anotherOrganizer
                )
        );

        assertEquals(EventStatus.DRAFT, event.getStatus());

        verify(eventRepository, never()).save(any());
    }

    @Test
    void ownerShouldCancelEvent() {
        User organizer = organizer(1L);
        Event event = publishedEvent(10L, organizer);

        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));

        eventService.cancelEvent(10L, organizer);

        assertEquals(EventStatus.CANCELLED, event.getStatus());

        verify(eventRepository).save(event);
    }

    @Test
    void nonOwnerShouldNotCancelEvent() {
        User owner = organizer(1L);
        User anotherOrganizer = organizer(2L);

        Event event = publishedEvent(10L, owner);

        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));

        assertThrows(
                UnauthorizedOperationException.class,
                () -> eventService.cancelEvent(
                        10L,
                        anotherOrganizer
                )
        );

        verify(eventRepository, never()).save(any());
    }

    @Test
    void shouldFindOrganizerEvents() {
        User organizer = organizer(1L);

        Event event1 = publishedEvent(1L, organizer);
        Event event2 = publishedEvent(2L, organizer);

        when(userRepository.findById(1L)).thenReturn(Optional.of(organizer));

        when(eventRepository.findByOrganizerId(1L)).thenReturn(List.of(event1, event2));

        List<EventResponse> response = eventService.findOrganizerEvents(1L);

        assertEquals(2, response.size());
    }

    @Test
    void shouldThrowWhenOrganizerDoesNotExist() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> eventService.findOrganizerEvents(99L)
        );

        verify(eventRepository, never()).findByOrganizerId(anyLong());
    }
}
