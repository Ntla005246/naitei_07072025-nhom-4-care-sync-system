package com.example.backend.controller.doctor;

import com.example.backend.constant.ApiConstants;
import com.example.backend.dto.ApiResponse;
import com.example.backend.dto.ScheduleTemplateDto;
import com.example.backend.dto.ScheduleTemplateUpsertRequest;
import com.example.backend.service.DoctorScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiConstants.DOCTOR_SCHEDULE_TEMPLATES_ENDPOINT)
@PreAuthorize("hasRole('DOCTOR')")
public class DoctorScheduleController {

    private final DoctorScheduleService scheduleService;
    private final MessageSource messageSource;

    public DoctorScheduleController(DoctorScheduleService scheduleService,
            MessageSource messageSource) {
        this.scheduleService = scheduleService;
        this.messageSource = messageSource;
    }

    @Operation(summary = "Create a working schedule", description = "Doctor creates a new working hour schedule")
    @PostMapping
    public ApiResponse<ScheduleTemplateDto> createTemplate(
            @Valid @RequestBody ScheduleTemplateUpsertRequest req) {
        var data = scheduleService.createTemplate(req);
        String msg = messageSource.getMessage("success.workinghour.upserted", null,
                LocaleContextHolder.getLocale());
        return ApiResponse.success(data, msg);
    }

    @Operation(summary = "Update a working schedule", description = "Doctor updates an existing working hour schedule by ID")
    @PutMapping("/{id}")
    public ApiResponse<ScheduleTemplateDto> updateTemplate(@PathVariable("id") Long id,
            @Valid @RequestBody ScheduleTemplateUpsertRequest req) {
        var data = scheduleService.updateTemplate(id, req);
        String msg = messageSource.getMessage("success.workinghour.upserted", null,
                LocaleContextHolder.getLocale());
        return ApiResponse.success(data, msg);
    }
}
