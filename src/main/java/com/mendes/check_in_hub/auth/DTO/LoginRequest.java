package com.mendes.check_in_hub.auth.DTO;

public record LoginRequest(
        String email,
        String password
) {
}
