package com.example.backend.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record ScheduleTemplateDto(Long id, DayOfWeek dayOfWeek, LocalTime startTime,
        LocalTime endTime) {
}
