package com.mendes.check_in_hub.user.DTO;

import com.mendes.check_in_hub.user.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserRequest(
        @NotBlank(message = "The name is mandatory")
        @Size(max = 150, message = "The  title name have a maximum of 150 characters")
        String name,

        @NotBlank(message = "The email is mandatory")
        @Email(message = "The format of email is invalid")
        String email,

        @NotBlank(message = "The password is mandatory")
        @Size(min = 6, max = 100, message = "The  password name have a minimum of 6 characters")
        String password,

        @NotNull(message = "The user role is mandatory.")
        UserRole role
) {}
