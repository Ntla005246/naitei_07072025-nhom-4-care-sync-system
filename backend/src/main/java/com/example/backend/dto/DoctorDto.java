package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDto {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String title;
    private String specialtyName;
    private Integer experienceYears;
    private String bio;
    private BigDecimal consultationFee;
}

