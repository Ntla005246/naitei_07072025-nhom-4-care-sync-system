package com.example.backend.service;

import com.example.backend.dto.ScheduleTemplateDto;
import com.example.backend.dto.ScheduleTemplateUpsertRequest;

public interface DoctorScheduleService {

    ScheduleTemplateDto createTemplate(ScheduleTemplateUpsertRequest req);

    ScheduleTemplateDto updateTemplate(Long templateId, ScheduleTemplateUpsertRequest req);
}
