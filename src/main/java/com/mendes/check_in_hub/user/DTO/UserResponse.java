package com.mendes.check_in_hub.user.DTO;

import com.mendes.check_in_hub.user.User;
import com.mendes.check_in_hub.user.UserRole;

public record UserResponse(
        Long id,
        String name,
        String email,
        UserRole role
) {
    public static UserResponse fromEntity (User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }
}
