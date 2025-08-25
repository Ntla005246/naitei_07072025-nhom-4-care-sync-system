package com.example.backend.mapper;

import com.example.backend.dto.ScheduleTemplateDto;
import com.example.backend.entity.DoctorWorkingHours;

public class DoctorScheduleMapper {

    private DoctorScheduleMapper() {
    }

    public static ScheduleTemplateDto toDto(DoctorWorkingHours e) {
        if (e == null)
            return null;
        return new ScheduleTemplateDto(e.getId(), java.time.DayOfWeek.of(e.getDayOfWeek()),
                e.getStartTime(), e.getEndTime());
    }
}
