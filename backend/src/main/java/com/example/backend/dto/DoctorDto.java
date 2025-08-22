package com.example.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record DoctorDto(Long id, String fullName,
        @Email(message = "email must be valid") String email,
        @Pattern(regexp = "^[+\\d\\s().-]{8,20}$", message = "phone number is invalid") String phone,
        String title, String specialtyName,
        @PositiveOrZero(message = "experienceYears must be >= 0") Integer experienceYears,
        String bio,
        @DecimalMin(value = "0.0", inclusive = true, message = "consultationFee must be >= 0") BigDecimal consultationFee) {
}
