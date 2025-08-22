package com.example.backend.event;

import java.time.LocalDateTime;

public record AppointmentReminderEvent(Long appointmentId, Long patientId, String patientEmail,
        String patientName, LocalDateTime startTime, Long doctorId, String doctorName) {
}
