package com.mendes.check_in_hub.enrollment.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record EnrollmentRequest(
        @NotNull(message = "The event ID is mandatory")
        @Positive(message = "The event ID must be greater than zero")
        Long eventId
) {}
