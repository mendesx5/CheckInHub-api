package com.mendes.check_in_hub.enrollment.DTO;

import com.mendes.check_in_hub.enrollment.Enrollment;
import com.mendes.check_in_hub.enrollment.EnrollmentStatus;
import com.mendes.check_in_hub.event.DTO.EventResponse;
import com.mendes.check_in_hub.user.DTO.UserResponse;

import java.time.LocalDateTime;

public record EnrollmentResponse(
        Long id,
        EventResponse event,
        UserResponse participant,
        String qrCodeToken,
        EnrollmentStatus status,
        LocalDateTime dateEnrollment
) {
    public static EnrollmentResponse fromEntity (Enrollment enrollment) {
        return new EnrollmentResponse(
                enrollment.getId(),
                EventResponse.fromEntity(enrollment.getEvent()),
                UserResponse.fromEntity(enrollment.getParticipant()),
                enrollment.getQrCodeToken(),
                enrollment.getStatus(),
                enrollment.getDateEnrollment()
        );
    }
}
