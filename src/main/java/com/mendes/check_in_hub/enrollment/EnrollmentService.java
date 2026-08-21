package com.mendes.check_in_hub.enrollment;

import com.google.zxing.WriterException;
import com.mendes.check_in_hub.checkin.CheckInRepository;
import com.mendes.check_in_hub.enrollment.DTO.EnrollmentRequest;
import com.mendes.check_in_hub.enrollment.DTO.EnrollmentResponse;
import com.mendes.check_in_hub.event.Event;
import com.mendes.check_in_hub.event.EventRepository;
import com.mendes.check_in_hub.event.EventStatus;
import com.mendes.check_in_hub.qrcode.QrCodeService;
import com.mendes.check_in_hub.user.User;
import com.mendes.check_in_hub.user.UserRepository;
import com.mendes.check_in_hub.user.UserRole;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CheckInRepository checkInRepository;
    private final QrCodeService qrCodeService;

    @Transactional
    public EnrollmentResponse createEnrollment (EnrollmentRequest request, User participant) {
        Event event = eventRepository.findById(request.eventId())
                .orElseThrow(() -> new RuntimeException("Event not found"));

        // Check if the user has the PARTICIPANT role.
        if (participant.getRole() != UserRole.PARTICIPANT) {
            throw new RuntimeException("Participant not allowed to enroll");
        }

        // Check if the event is PUBLISHED.
        if (event.getStatus() != EventStatus.PUBLISHED) {
            throw new RuntimeException("Event is not open for enrollment");
        }

        // Check if the event date is in the future.
        if (event.getDateTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("This event has already passed");
        }

        // Check if a registration for this participant already exists for the event.
        boolean alreadyEnrolled = enrollmentRepository.existsByEventIdAndParticipantId(
                event.getId(),
                participant.getId()
        );
        if (alreadyEnrolled) {
            throw new RuntimeException("This event has already been enrolled");
        }

        // Count CONFIRMED registrations and compare with capacity.
        long confirmedEnrollments = enrollmentRepository.countByEventIdAndStatus(
                event.getId(),
                EnrollmentStatus.CONFIRMED
        );
        if  (confirmedEnrollments >= event.getCapacity()) {
            throw new RuntimeException("This event is already fully booked");
        }

        // Generate token
        String token = UUID.randomUUID().toString();

        Enrollment enrollment = Enrollment.builder()
                .event(event)
                .participant(participant)
                .qrCodeToken(token)
                .status(EnrollmentStatus.CONFIRMED)
                .dateEnrollment(LocalDateTime.now())
                .build();

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        return EnrollmentResponse.fromEntity(savedEnrollment);

    }

    @Transactional
    public EnrollmentResponse findByEnrollmentId (Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found with id: " + enrollmentId));

        return EnrollmentResponse.fromEntity(enrollment);
    }

    @Transactional
    public List<EnrollmentResponse> findAllEnrollments () {
        return enrollmentRepository.findAll()
                .stream()
                .map(EnrollmentResponse::fromEntity)
                .toList();
    }

    @Transactional
    public List<EnrollmentResponse> findMyEnrollments (User participant) {
        List<Enrollment> enrollments = enrollmentRepository.findByParticipantId(participant.getId());

        return enrollments.stream()
                .map(EnrollmentResponse::fromEntity)
                .toList();
    }

    @Transactional
    public List<EnrollmentResponse> findEnrollmentsByEvent (Long eventId, User organizer) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if (!event.getOrganizer().getId().equals(organizer.getId())) {
            throw new RuntimeException("You are not allowed to view this event enrollments");
        }

        List<Enrollment> enrollments = enrollmentRepository.findByEventId(eventId);

        return enrollments.stream()
                .map(EnrollmentResponse::fromEntity)
                .toList();
    }


    @Transactional
    public void cancelEnrollment (Long enrollmentId, User participant) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found with id: " + enrollmentId));

        if (!enrollment.getParticipant().getId().equals(participant.getId())) {
            throw new RuntimeException("You are not allowed to cancel this enrollment");
        }

        if (enrollment.getStatus() == EnrollmentStatus.CANCELED) {
            throw new RuntimeException("Enrollment is already canceled");
        }

        enrollment.setStatus(EnrollmentStatus.CANCELED);
        enrollmentRepository.save(enrollment);
    }

    @Transactional
    public byte[] generateEnrollmentQrCode (Long enrollmentId, User participant) throws IOException, WriterException {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found with id: " + enrollmentId));

        if (!enrollment.getParticipant().getId().equals(participant.getId())) {
            throw new RuntimeException("You are not allowed to access this QR Code");
        }

        if (enrollment.getStatus() == EnrollmentStatus.CANCELED) {
            throw new RuntimeException("Canceled enrollment does not have a valid QR Code");
        }

        try {
            return qrCodeService.generateQrCode(enrollment.getQrCodeToken());
        } catch (Exception e) {
            throw new RuntimeException("QR code generation failed");
        }
    }

}
