package com.mendes.check_in_hub.enrollment;

import com.mendes.check_in_hub.enrollment.DTO.EnrollmentRequest;
import com.mendes.check_in_hub.enrollment.DTO.EnrollmentResponse;
import com.mendes.check_in_hub.event.Event;
import com.mendes.check_in_hub.event.EventRepository;
import com.mendes.check_in_hub.event.EventStatus;
import com.mendes.check_in_hub.exception.BusinessRuleException;
import com.mendes.check_in_hub.exception.EnrollmentNotFoundException;
import com.mendes.check_in_hub.exception.EventNotFoundException;
import com.mendes.check_in_hub.exception.UnauthorizedOperationException;
import com.mendes.check_in_hub.qrcode.QrCodeService;
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
class EnrollmentServiceTest {
    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private QrCodeService qrCodeService;

    @InjectMocks
    private EnrollmentService enrollmentService;

    private User participant(Long id) {
        return User.builder()
                .id(id)
                .name("Participant")
                .email("participant" + id + "@test.com")
                .role(UserRole.PARTICIPANT)
                .build();
    }

    private User organizer(Long id) {
        return User.builder()
                .id(id)
                .name("Organizer")
                .email("organizer" + id + "@test.com")
                .role(UserRole.ORGANIZER)
                .build();
    }

    private Event event(Long id, User organizer) {
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

    private Enrollment enrollment(
            Long id,
            Event event,
            User participant
    ) {
        return Enrollment.builder()
                .id(id)
                .event(event)
                .participant(participant)
                .qrCodeToken("token-test")
                .status(EnrollmentStatus.CONFIRMED)
                .dateEnrollment(LocalDateTime.now())
                .build();
    }

    @Test
    void participantShouldEnrollInPublishedEvent() {
        User participant = participant(1L);
        Event event = event(10L, organizer(2L));

        EnrollmentRequest request =
                new EnrollmentRequest(10L);

        when(eventRepository.findById(10L))
                .thenReturn(Optional.of(event));

        when(enrollmentRepository
                .existsByEventIdAndParticipantId(10L, 1L))
                .thenReturn(false);

        when(enrollmentRepository
                .countByEventIdAndStatus(10L, EnrollmentStatus.CONFIRMED))
                .thenReturn(0L);

        when(enrollmentRepository.save(any(Enrollment.class)))
                .thenAnswer(invocation -> {
                    Enrollment saved = invocation.getArgument(0);
                    saved.setId(1L);
                    return saved;
                });

        EnrollmentResponse response =
                enrollmentService.createEnrollment(request, participant);

        assertNotNull(response);
        assertEquals(EnrollmentStatus.CONFIRMED, response.status());
        assertNotNull(response.qrCodeToken());
        assertFalse(response.qrCodeToken().isBlank());

        verify(enrollmentRepository).save(any(Enrollment.class));
    }

    @Test
    void organizerShouldNotEnrollInEvent() {
        User organizer = organizer(1L);
        Event event = event(10L, organizer(2L));

        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));

        assertThrows(
                BusinessRuleException.class,
                () -> enrollmentService.createEnrollment(
                        new EnrollmentRequest(10L),
                        organizer
                )
        );

        verify(enrollmentRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenEventDoesNotExist() {
        when(eventRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                EventNotFoundException.class,
                () -> enrollmentService.createEnrollment(
                        new EnrollmentRequest(99L),
                        participant(1L)
                )
        );
    }

    @Test
    void participantShouldNotEnrollInDraftEvent() {
        Event event = event(10L, organizer(2L));
        event.setStatus(EventStatus.DRAFT);

        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));

        assertThrows(
                BusinessRuleException.class,
                () -> enrollmentService.createEnrollment(
                        new EnrollmentRequest(10L),
                        participant(1L)
                )
        );

        verify(enrollmentRepository, never()).save(any());
    }

    @Test
    void participantShouldNotEnrollInPastEvent() {
        Event event = event(10L, organizer(2L));
        event.setDateTime(LocalDateTime.now().minusDays(1));

        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));

        assertThrows(
                BusinessRuleException.class,
                () -> enrollmentService.createEnrollment(
                        new EnrollmentRequest(10L),
                        participant(1L)
                )
        );
    }

    @Test
    void participantShouldNotEnrollTwice() {
        User participant = participant(1L);
        Event event = event(10L, organizer(2L));

        when(eventRepository.findById(10L))
                .thenReturn(Optional.of(event));

        when(enrollmentRepository
                .existsByEventIdAndParticipantId(10L, 1L))
                .thenReturn(true);

        assertThrows(
                BusinessRuleException.class,
                () -> enrollmentService.createEnrollment(
                        new EnrollmentRequest(10L),
                        participant
                )
        );

        verify(enrollmentRepository, never()).save(any());
    }

    @Test
    void participantShouldNotEnrollInFullEvent() {
        Event event = event(10L, organizer(2L));
        event.setCapacity(2);

        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));

        when(enrollmentRepository
                .existsByEventIdAndParticipantId(10L, 1L))
                .thenReturn(false);

        when(enrollmentRepository
                .countByEventIdAndStatus(
                        10L,
                        EnrollmentStatus.CONFIRMED
                ))
                .thenReturn(2L);

        assertThrows(
                BusinessRuleException.class,
                () -> enrollmentService.createEnrollment(
                        new EnrollmentRequest(10L),
                        participant(1L)
                )
        );

        verify(enrollmentRepository, never()).save(any());
    }

    @Test
    void shouldFindEnrollmentById() {
        User participant = participant(1L);
        Event event = event(10L, organizer(2L));

        Enrollment enrollment = enrollment(5L, event, participant);

        when(enrollmentRepository.findById(5L)).thenReturn(Optional.of(enrollment));

        EnrollmentResponse response = enrollmentService.findByEnrollmentId(5L);

        assertEquals(5L, response.id());
        assertEquals(participant.getId(), response.participant().id());
    }

    @Test
    void shouldThrowWhenEnrollmentDoesNotExist() {
        when(enrollmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                EnrollmentNotFoundException.class,
                () -> enrollmentService.findByEnrollmentId(99L)
        );
    }

    @Test
    void shouldFindMyEnrollments() {
        User participant = participant(1L);
        Event event = event(10L, organizer(2L));

        when(enrollmentRepository
                .findByParticipantId(1L))
                .thenReturn(List.of(
                        enrollment(1L, event, participant),
                        enrollment(2L, event, participant)
                ));

        List<EnrollmentResponse> response = enrollmentService.findMyEnrollments(participant);

        assertEquals(2, response.size());

        verify(enrollmentRepository).findByParticipantId(1L);
    }

    @Test
    void ownerShouldViewEventEnrollments() {
        User organizer = organizer(1L);
        User participant = participant(2L);

        Event event = event(10L, organizer);

        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));

        when(enrollmentRepository.findByEventId(10L)).thenReturn(List.of(enrollment(1L, event, participant)));

        List<EnrollmentResponse> response = enrollmentService.findEnrollmentsByEvent(10L, organizer);

        assertEquals(1, response.size());
    }

    @Test
    void nonOwnerShouldNotViewEventEnrollments() {
        Event event = event(10L, organizer(1L));

        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));

        assertThrows(UnauthorizedOperationException.class,
                () -> enrollmentService.findEnrollmentsByEvent(10L, organizer(2L))
        );

        verify(enrollmentRepository, never()).findByEventId(anyLong());
    }

    @Test
    void participantShouldCancelOwnEnrollment() {
        User participant = participant(1L);

        Enrollment enrollment = enrollment(
                5L,
                event(10L, organizer(2L)),
                participant
        );

        when(enrollmentRepository.findById(5L)).thenReturn(Optional.of(enrollment));

        enrollmentService.cancelEnrollment(
                5L,
                participant
        );

        assertEquals(EnrollmentStatus.CANCELED, enrollment.getStatus());

        verify(enrollmentRepository).save(enrollment);
    }

    @Test
    void participantShouldNotCancelAnotherEnrollment() {
        Enrollment enrollment = enrollment(
                5L,
                event(10L, organizer(2L)),
                participant(1L)
        );

        when(enrollmentRepository.findById(5L)).thenReturn(Optional.of(enrollment));

        assertThrows(
                UnauthorizedOperationException.class,
                () -> enrollmentService.cancelEnrollment(
                        5L,
                        participant(99L)
                )
        );

        verify(enrollmentRepository, never()).save(any());
    }

    @Test
    void shouldNotCancelEnrollmentTwice() {
        User participant = participant(1L);

        Enrollment enrollment = enrollment(
                5L,
                event(10L, organizer(2L)),
                participant
        );

        enrollment.setStatus(EnrollmentStatus.CANCELED);

        when(enrollmentRepository.findById(5L)).thenReturn(Optional.of(enrollment));

        assertThrows(
                BusinessRuleException.class,
                () -> enrollmentService.cancelEnrollment(
                        5L,
                        participant
                )
        );
    }

    @Test
    void ownerShouldGenerateQrCode() throws Exception {
        User participant = participant(1L);

        Enrollment enrollment = enrollment(
                5L,
                event(10L, organizer(2L)),
                participant
        );

        byte[] expectedQrCode = {1, 2, 3};

        when(enrollmentRepository.findById(5L)).thenReturn(Optional.of(enrollment));

        when(qrCodeService.generateQrCode("token-test")).thenReturn(expectedQrCode);

        byte[] result = enrollmentService.generateEnrollmentQrCode(5L, participant);

        assertArrayEquals(expectedQrCode, result);

        verify(qrCodeService).generateQrCode("token-test");
    }

    @Test
    void nonOwnerShouldNotAccessQrCode() {
        Enrollment enrollment = enrollment(
                5L,
                event(10L, organizer(2L)),
                participant(1L)
        );

        when(enrollmentRepository.findById(5L)).thenReturn(Optional.of(enrollment));

        assertThrows(
                UnauthorizedOperationException.class,
                () -> enrollmentService.generateEnrollmentQrCode(
                        5L,
                        participant(99L)
                )
        );
    }

    @Test
    void canceledEnrollmentShouldNotGenerateQrCode() {
        User participant = participant(1L);

        Enrollment enrollment = enrollment(
                5L,
                event(10L, organizer(2L)),
                participant
        );

        enrollment.setStatus(EnrollmentStatus.CANCELED);

        when(enrollmentRepository.findById(5L)).thenReturn(Optional.of(enrollment));

        assertThrows(
                BusinessRuleException.class,
                () -> enrollmentService.generateEnrollmentQrCode(
                        5L,
                        participant
                )
        );

        verifyNoInteractions(qrCodeService);
    }
}
