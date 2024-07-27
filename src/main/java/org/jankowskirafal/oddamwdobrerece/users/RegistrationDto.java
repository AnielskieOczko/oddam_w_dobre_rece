package org.jankowskirafal.oddamwdobrerece.users;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegistrationDto(
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Password is required")
        String password,

        @NotBlank(message = "Password confirmation is required")
        String confirmPassword
) { }
