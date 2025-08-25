package com.example.backend.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record DoctorNewAppointmentRequestEvent(Long appointmentId, Long doctorId,
        String doctorEmail, String doctorName, String specialtyName, Long patientId,
        String patientName, String patientEmail, String patientPhone, List<String> serviceNames,
        BigDecimal totalPrice, String patientNotes, LocalDateTime appointmentCreatedAt,
        LocalDateTime appointmentStartTime, LocalDateTime appointmentEndTime) {
}
