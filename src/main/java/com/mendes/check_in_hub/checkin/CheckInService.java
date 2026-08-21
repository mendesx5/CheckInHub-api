package com.mendes.check_in_hub.checkin;

import com.mendes.check_in_hub.checkin.DTO.CheckInRequest;
import com.mendes.check_in_hub.checkin.DTO.CheckInResponse;
import com.mendes.check_in_hub.enrollment.Enrollment;
import com.mendes.check_in_hub.enrollment.EnrollmentRepository;

import com.mendes.check_in_hub.enrollment.EnrollmentStatus;
import com.mendes.check_in_hub.event.Event;
import com.mendes.check_in_hub.event.EventRepository;
import com.mendes.check_in_hub.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckInService {

    private final CheckInRepository checkInRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final EventRepository eventRepository;

    @Transactional
    public CheckInResponse createCheckIn (CheckInRequest request, User validator) {

        String token = request.qrCodeToken();

        Enrollment enrollment = enrollmentRepository.findByQrCodeToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid QR Code"));

        // Check if the enrollment is confirmed.
        if (enrollment.getStatus() != EnrollmentStatus.CONFIRMED) {
            throw new RuntimeException("Invalid Enrollment");
        }

        // Check if check-in has already taken place.
        boolean alreadyCheckIn = checkInRepository.existsByEnrollmentId(enrollment.getId());
        if (alreadyCheckIn) {
            throw new RuntimeException("Check-in already completed");
        }

        // Check if the user is the event organizer
        if (!enrollment.getEvent().getOrganizer().getId().equals(validator.getId())) {
            throw new RuntimeException("User not allowed to validate check-in");
        }

        CheckIn checkIn = CheckIn.builder()
                .enrollment(enrollment)
                .checkInDateTime(LocalDateTime.now())
                .validatedBy(validator)
                .build();

        CheckIn savedCheckIn = checkInRepository.save(checkIn);
        return CheckInResponse.fromEntity(savedCheckIn);

    }

    @Transactional
    public List<CheckInResponse> findCheckInsByEvent (Long eventId, User organizer) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if (!event.getOrganizer().getId().equals(organizer.getId())) {
            throw new RuntimeException("You are not allowed to view this event checkins");
        }

        List<CheckIn> checkIns = checkInRepository.findByEnrollmentEventId(eventId);

        return checkIns.stream()
                .map(CheckInResponse::fromEntity)
                .toList();
    }

}
