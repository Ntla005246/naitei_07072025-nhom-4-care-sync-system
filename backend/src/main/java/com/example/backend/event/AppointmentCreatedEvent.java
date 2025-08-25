package com.example.backend.event;

import java.time.LocalDateTime;

import java.math.BigDecimal;

public record AppointmentCreatedEvent(Long appointmentId, Long patientId, String patientEmail,
        String patientName, LocalDateTime startTime, LocalDateTime endTime, Long doctorId,
        String doctorName, String specialtyName, BigDecimal totalPrice) {
}
