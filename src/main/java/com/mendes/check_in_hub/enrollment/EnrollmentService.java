package com.mendes.check_in_hub.enrollment;

import com.mendes.check_in_hub.enrollment.DTO.EnrollmentRequest;
import com.mendes.check_in_hub.enrollment.DTO.EnrollmentResponse;
import com.mendes.check_in_hub.event.Event;
import com.mendes.check_in_hub.event.EventRepository;
import com.mendes.check_in_hub.event.EventStatus;
import com.mendes.check_in_hub.user.User;
import com.mendes.check_in_hub.user.UserRepository;
import com.mendes.check_in_hub.user.UserRole;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Transactional
    public EnrollmentResponse createEnrollment (EnrollmentRequest request) {
        Event event = eventRepository.findById(request.eventId())
                .orElseThrow(() -> new RuntimeException("Event not found"));

        User participant = userRepository.findById(request.participantId())
                .orElseThrow(() -> new RuntimeException("Participant not found"));

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
    public void cancelEnrollment (Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found with id: " + enrollmentId));

        enrollment.setStatus(EnrollmentStatus.CANCELED);
        enrollmentRepository.save(enrollment);
    }



}
