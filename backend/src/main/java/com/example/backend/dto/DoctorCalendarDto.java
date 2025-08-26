package com.example.backend.dto;

import com.example.backend.entity.DoctorCalendar;
import java.time.LocalDateTime;

public record DoctorCalendarDto(Long id, Long doctorId, String doctorName, String doctorEmail,
        DoctorCalendar.CalendarType calendarType, String calendarId, LocalDateTime tokenExpiry,
        boolean isActive, LocalDateTime createdAt, LocalDateTime updatedAt) {
    public static DoctorCalendarDto fromEntity(DoctorCalendar calendar) {
        return new DoctorCalendarDto(calendar.getId(), calendar.getDoctor().getId(),
                calendar.getDoctor().getUser().getFullName(),
                calendar.getDoctor().getUser().getEmail(), calendar.getCalendarType(),
                calendar.getCalendarId(), calendar.getTokenExpiry(), calendar.isActive(),
                calendar.getCreatedAt(), calendar.getUpdatedAt());
    }
}
