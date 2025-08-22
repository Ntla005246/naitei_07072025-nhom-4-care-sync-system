package com.example.backend.dto;

import java.time.LocalDate;

import com.example.backend.constant.enums.Gender;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserProfileDto(
        @NotBlank(message = "{validation.fullname.required}") @Size(min = 2, max = 100, message = "{validation.fullname.length}") String fullName,

        @NotBlank(message = "{validation.email.required}") @Email(message = "{validation.email.invalid}") @Size(max = 120, message = "{validation.email.length}") String email,

        @Pattern(regexp = "^[0-9+\\-()\\s]*$", message = "{validation.phone.pattern}") @Size(max = 20, message = "{validation.phone.length}") String phoneNumber,

        Gender gender,

        LocalDate dateOfBirth,

        @Size(max = 500, message = "{validation.address.length}") String address) {
}
