package com.mendes.check_in_hub.event.DTO;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public record EventRequest(
        @NotBlank(message = "The title is mandatory")
        @Size(max = 150, message = "The  title must have a maximum of 150 characters")
        String title,

        String description,

        @NotNull(message = "The date and time are mandatory")
        @Future(message = "The date of event must be in the future")
        LocalDateTime dateTime,

        @NotBlank(message = "The location is mandatory")
        @Size(max = 255, message = "The  location must have a maximum of 255 characters")
        String location,

        @Min(value = 1, message = "The minimum capacity is 1 participant")
        int capacity
) {}
