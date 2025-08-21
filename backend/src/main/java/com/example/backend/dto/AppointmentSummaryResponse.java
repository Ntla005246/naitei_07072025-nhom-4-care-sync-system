package com.example.backend.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class AppointmentSummaryResponse {
    private Long id;
    private String doctorName;
    private String specialtyName;
    private LocalDateTime appointmentTime;
    private String status;
}
