package com.mendes.check_in_hub.enrollment.DTO;

import com.mendes.check_in_hub.event.Event;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EnrollmentRequest(
        @NotNull(message = "The event ID is mandatory")
        Event eventId
) {}
