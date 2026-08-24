package com.mendes.check_in_hub.checkin.DTO;

import jakarta.validation.constraints.NotBlank;

public record CheckInRequest(
        @NotBlank(message = "The QR Code token is mandatory")
        String qrCodeToken
) {}
