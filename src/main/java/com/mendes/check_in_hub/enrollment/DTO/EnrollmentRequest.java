package com.mendes.check_in_hub.enrollment.DTO;

import jakarta.validation.constraints.NotNull;

public record EnrollmentRequest(
        @NotNull(message = "The event ID is mandatory")
        Long eventId,

        @NotNull(message = "The participant ID is mandatory")
        Long participantId
) {}
