package com.mendes.check_in_hub.checkin.DTO;

import com.mendes.check_in_hub.checkin.CheckIn;
import com.mendes.check_in_hub.user.DTO.UserResponse;

import java.time.LocalDateTime;

public record CheckInResponse(
        Long id,
        Long enrollmentId,
        String participantName,
        String eventTitle,
        LocalDateTime checkInDateTime,
        UserResponse validatedBy
) {
    public static CheckInResponse fromEntity(CheckIn checkIn) {
        return new CheckInResponse(
                checkIn.getId(),
                checkIn.getEnrollment().getId(),
                checkIn.getEnrollment().getParticipant().getName(),
                checkIn.getEnrollment().getEvent().getTitle(),
                checkIn.getCheckInDateTime(),
                checkIn.getValidatedById() != null ? UserResponse.fromEntity(checkIn.getValidatedById()) : null
        );
    }
}
