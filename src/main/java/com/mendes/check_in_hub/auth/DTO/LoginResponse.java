package com.mendes.check_in_hub.auth.DTO;

import com.mendes.check_in_hub.user.DTO.UserResponse;

public record LoginResponse(
        String token,
        UserResponse user
) {
}
