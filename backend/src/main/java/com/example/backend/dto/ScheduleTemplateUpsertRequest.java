package com.example.backend.dto;

import jakarta.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalTime;

public record ScheduleTemplateUpsertRequest(@NotNull DayOfWeek dayOfWeek,
        @NotNull LocalTime startTime, @NotNull LocalTime endTime) {
}
