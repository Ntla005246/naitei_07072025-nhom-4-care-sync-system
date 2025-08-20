package com.example.backend.controller;

import com.example.backend.constant.ApiConstants;

import com.example.backend.dto.*;
import com.example.backend.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@RestController
@RequestMapping(ApiConstants.APPOINTMENTS_ENDPOINT)
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final MessageSource messageSource;

    @PostMapping
    @Operation(summary = "Create appointment from an AVAILABLE slot")
    public ApiResponse<AppointmentCreateResponse> create(
            @Valid @RequestBody AppointmentCreateRequest request) {
        log.info("Create appointment: {}", request);
        var resp = appointmentService.create(request);
        String message = messageSource.getMessage("success.appointment.created", null,
                LocaleContextHolder.getLocale());
        return ApiResponse.success(resp, message);

    }
    @PutMapping("/{id}/reschedule")
    @Operation(summary = "Reschedule an appointment to a new AVAILABLE slot")
    public ApiResponse<AppointmentRescheduleResponse> reschedule(
            @PathVariable("id") Long appointmentId,
            @Valid @RequestBody AppointmentRescheduleRequest request) {

        log.info("Reschedule appointment id={}, newSlotId={}, patientId={}, confirmPolicy={}",
                appointmentId, request.newSlotId(), request.patientId(), request.confirmPolicy());

        AppointmentRescheduleResponse resp = appointmentService.reschedule(appointmentId, request);

        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.appointment.rescheduled", null, locale);

        return ApiResponse.success(resp, message);
    }
}
