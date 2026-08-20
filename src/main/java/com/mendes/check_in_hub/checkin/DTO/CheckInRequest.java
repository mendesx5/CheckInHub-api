package com.mendes.check_in_hub.checkin.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CheckInRequest(
        @NotBlank
        String qrCodeToken
) {}
