package com.example.backend.dto;

import java.time.LocalDateTime;

public record AppointmentSummaryDto(Long appointmentId, LocalDateTime startTime,
        LocalDateTime endTime, String status, Long patientId, String patientName) {
}
