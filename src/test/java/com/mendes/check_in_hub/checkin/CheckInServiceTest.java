package com.mendes.check_in_hub.checkin;

import com.mendes.check_in_hub.checkin.DTO.CheckInRequest;
import com.mendes.check_in_hub.checkin.DTO.CheckInResponse;
import com.mendes.check_in_hub.enrollment.Enrollment;
import com.mendes.check_in_hub.enrollment.EnrollmentRepository;
import com.mendes.check_in_hub.enrollment.EnrollmentStatus;
import com.mendes.check_in_hub.event.Event;
import com.mendes.check_in_hub.event.EventRepository;
import com.mendes.check_in_hub.event.EventStatus;
import com.mendes.check_in_hub.exception.BusinessRuleException;
import com.mendes.check_in_hub.exception.EventNotFoundException;
import com.mendes.check_in_hub.exception.UnauthorizedOperationException;
import com.mendes.check_in_hub.user.User;
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
class CheckInServiceTest {
    @Mock
    private CheckInRepository checkInRepository;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private CheckInService checkInService;

    private User organizer(Long id) {
        return User.builder()
                .id(id)
                .name("Organizer")
                .email("organizer@test.com")
                .role(UserRole.ORGANIZER)
                .build();
    }

    private User participant(Long id) {
        return User.builder()
                .id(id)
                .name("Participant")
                .email("participant@test.com")
                .role(UserRole.PARTICIPANT)
                .build();
    }

    private Event event(User organizer) {
        return Event.builder()
                .id(10L)
                .title("Java Event")
                .description("Test")
                .dateTime(LocalDateTime.now().plusDays(5))
                .location("Natal/RN")
                .capacity(100)
                .status(EventStatus.PUBLISHED)
                .organizer(organizer)
                .build();
    }

    private Enrollment enrollment(User participant, Event event) {
        return Enrollment.builder()
                .id(5L)
                .participant(participant)
                .event(event)
                .qrCodeToken("valid-token")
                .status(EnrollmentStatus.CONFIRMED)
                .dateEnrollment(LocalDateTime.now())
                .build();
    }

    @Test
    void ownerShouldCreateCheckIn() {
        User organizer = organizer(1L);
        User participant = participant(2L);

        Event event = event(organizer);
        Enrollment enrollment = enrollment(participant, event);

        when(enrollmentRepository.findByQrCodeToken("valid-token")).thenReturn(Optional.of(enrollment));

        when(checkInRepository.existsByEnrollmentId(5L)).thenReturn(false);

        when(checkInRepository.save(any(CheckIn.class)))
                .thenAnswer(invocation -> {
                    CheckIn checkIn = invocation.getArgument(0);

                    checkIn.setId(1L);

                    return checkIn;
                });

        CheckInResponse response = checkInService.createCheckIn(new CheckInRequest("valid-token"), organizer);

        assertNotNull(response);
        assertEquals(5L, response.enrollmentId());
        assertEquals("Participant", response.participantName());
        assertEquals("Java Event", response.eventTitle());
        assertEquals(organizer.getId(), response.validatedBy().id());

        verify(checkInRepository).save(any(CheckIn.class));
    }

    @Test
    void invalidQrCodeShouldFail() {
        when(enrollmentRepository.findByQrCodeToken("invalid")).thenReturn(Optional.empty());

        assertThrows(
                BusinessRuleException.class,
                () -> checkInService.createCheckIn(
                        new CheckInRequest("invalid"),
                        organizer(1L)
                )
        );

        verify(checkInRepository, never()).save(any());
    }

    @Test
    void canceledEnrollmentShouldNotCheckIn() {
        User organizer = organizer(1L);

        Enrollment enrollment = enrollment(
                participant(2L),
                event(organizer)
        );

        enrollment.setStatus(EnrollmentStatus.CANCELED);

        when(enrollmentRepository.findByQrCodeToken("valid-token")).thenReturn(Optional.of(enrollment));

        assertThrows(
                BusinessRuleException.class,
                () -> checkInService.createCheckIn(
                        new CheckInRequest("valid-token"),
                        organizer
                )
        );
    }

    @Test
    void duplicatedCheckInShouldFail() {
        User organizer = organizer(1L);

        Enrollment enrollment = enrollment(
                participant(2L),
                event(organizer)
        );

        when(enrollmentRepository
                .findByQrCodeToken("valid-token"))
                .thenReturn(Optional.of(enrollment));

        when(checkInRepository
                .existsByEnrollmentId(5L))
                .thenReturn(true);

        assertThrows(
                BusinessRuleException.class,
                () -> checkInService.createCheckIn(
                        new CheckInRequest("valid-token"),
                        organizer
                )
        );

        verify(checkInRepository, never()).save(any());
    }

    @Test
    void nonOwnerShouldNotValidateCheckIn() {
        User owner = organizer(1L);
        User anotherOrganizer = organizer(2L);

        Enrollment enrollment = enrollment(participant(3L), event(owner));

        when(enrollmentRepository.findByQrCodeToken("valid-token")).thenReturn(Optional.of(enrollment));

        when(checkInRepository.existsByEnrollmentId(5L)).thenReturn(false);

        assertThrows(
                UnauthorizedOperationException.class,
                () -> checkInService.createCheckIn(
                        new CheckInRequest("valid-token"),
                        anotherOrganizer
                )
        );

        verify(checkInRepository, never()).save(any());
    }

    @Test
    void ownerShouldFindCheckInsByEvent() {
        User organizer = organizer(1L);

        Event event = event(organizer);
        Enrollment enrollment = enrollment(participant(2L), event);

        CheckIn checkIn = CheckIn.builder()
                .id(1L)
                .enrollment(enrollment)
                .checkInDateTime(LocalDateTime.now())
                .validatedBy(organizer)
                .build();

        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));

        when(checkInRepository.findByEnrollmentEventId(10L)).thenReturn(List.of(checkIn));

        List<CheckInResponse> response =
                checkInService.findCheckInsByEvent(
                        10L,
                        organizer
                );

        assertEquals(1, response.size());
        assertEquals(5L, response.getFirst().enrollmentId());
    }

    @Test
    void nonOwnerShouldNotFindCheckInsByEvent() {
        Event event = event(organizer(1L));

        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));

        assertThrows(
                UnauthorizedOperationException.class,
                () -> checkInService.findCheckInsByEvent(
                        10L,
                        organizer(2L)
                )
        );

        verify(checkInRepository, never()).findByEnrollmentEventId(anyLong());
    }

    @Test
    void shouldThrowWhenEventDoesNotExist() {
        when(eventRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                EventNotFoundException.class,
                () -> checkInService.findCheckInsByEvent(99L, organizer(1L))
        );
    }
}
