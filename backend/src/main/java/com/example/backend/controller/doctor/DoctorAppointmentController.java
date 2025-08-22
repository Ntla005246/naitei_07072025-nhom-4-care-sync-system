package com.example.backend.controller.doctor;

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

@RestController
@RequestMapping(ApiConstants.DOCTOR_APPOINTMENTS_ENDPOINT)
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Doctor Appointments", description = "Doctor appointment management APIs")
public class DoctorAppointmentController {

    private final AppointmentService appointmentService;
    private final MessageSource messageSource;

    @GetMapping("/me")
    @Operation(summary = "List current doctor's appointments with pagination and filters")
    public ApiResponse<PageResponse<AppointmentSummaryDto>> listMyAppointments(
            @Valid @ModelAttribute AppointmentListRequest request) {
        log.info("Listing my appointments: {}", request);
        PageResponse<AppointmentSummaryDto> result = appointmentService
                .listDoctorAppointments(request);
        String message = messageSource.getMessage("success.operation", null,
                LocaleContextHolder.getLocale());
        return ApiResponse.success(result, message);
    }

    @PutMapping("/{id}/confirm")
    @Operation(summary = "Doctor confirms an appointment")
    public ApiResponse<AppointmentCreateResponse> confirm(@PathVariable Long id) {
        log.info("Doctor confirming appointment: {}", id);
        var resp = appointmentService.confirm(id);
        String message = messageSource.getMessage("success.appointment.confirmed", null,
                LocaleContextHolder.getLocale());
        return ApiResponse.success(resp, message);
    }

    @PutMapping("/{id}/reject")
    @Operation(summary = "Doctor rejects an appointment")
    public ApiResponse<AppointmentCreateResponse> reject(@PathVariable Long id,
            @Valid @RequestBody AppointmentRejectRequest request) {
        log.info("Doctor rejecting appointment: {}, reason: {}", id, request.reason());
        var resp = appointmentService.reject(id, request);
        String message = messageSource.getMessage("success.appointment.rejected", null,
                LocaleContextHolder.getLocale());
        return ApiResponse.success(resp, message);
    }
}
