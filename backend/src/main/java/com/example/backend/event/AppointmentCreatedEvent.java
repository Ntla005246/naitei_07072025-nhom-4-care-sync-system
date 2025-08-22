package com.example.backend.event;

import java.time.LocalDateTime;

public record AppointmentCreatedEvent(Long appointmentId, Long patientId, String patientEmail,
        String patientName, LocalDateTime startTime, LocalDateTime endTime, Long doctorId,
        String doctorName, String specialtyName) {
}
