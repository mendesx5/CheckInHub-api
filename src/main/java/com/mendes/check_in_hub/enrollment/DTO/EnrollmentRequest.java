package com.mendes.check_in_hub.enrollment.DTO;

import com.mendes.check_in_hub.event.Event;
import jakarta.validation.constraints.NotBlank;

public record EnrollmentRequest(
        @NotBlank(message = "The event ID is mandatory")
        Event event
) {}
