package com.mendes.check_in_hub.checkin.DTO;

import jakarta.validation.constraints.NotBlank;

public record CheckInRequest(
        @NotBlank(message = "O token do QR Code é obrigatório")
        String qrCodeToken,
        Long validatedById
) {}
