package com.example.backend.controller.patient;

import com.example.backend.constant.ApiConstants;

import com.example.backend.dto.ApiResponse;
import com.example.backend.dto.AppointmentCreateRequest;
import com.example.backend.dto.AppointmentCreateResponse;
import com.example.backend.dto.AppointmentCancelRequest;
import com.example.backend.dto.AppointmentCancelResponse;
import com.example.backend.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.backend.dto.PageResponse;
import com.example.backend.dto.AppointmentSummaryResponse;
import com.example.backend.dto.AppointmentFilterRequest;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(ApiConstants.PATIENT_APPOINTMENTS_ENDPOINT)
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Patient Appointments", description = "Patient appointment management APIs")
public class PatientAppointmentController {

    private static final String ME_ENDPOINT = "/me";

    private final AppointmentService appointmentService;
    private final MessageSource messageSource;

    @GetMapping(ME_ENDPOINT)
    @Operation(summary = "Get my appointments with filters and pagination")
    public ResponseEntity<ApiResponse<PageResponse<AppointmentSummaryResponse>>> getMyAppointments(
            @Valid @ModelAttribute AppointmentFilterRequest filters, Pageable pageable) {

        Page<AppointmentSummaryResponse> appointmentsPage = appointmentService
                .getMyAppointments(filters, pageable);

        PageResponse<AppointmentSummaryResponse> pageResponse = PageResponse.of(appointmentsPage);

        String successMessage = messageSource.getMessage("success.appointments.retrieved", null,
                LocaleContextHolder.getLocale());

        return ResponseEntity.ok(ApiResponse.success(pageResponse, successMessage));
    }

    @PostMapping
    @Operation(summary = "Create an appointment from an AVAILABLE slot")
    public ResponseEntity<ApiResponse<AppointmentCreateResponse>> create(
            @Valid @RequestBody AppointmentCreateRequest request) {
        log.info("Request to create appointment: {}", request);
        AppointmentCreateResponse response = appointmentService.create(request);
        String message = messageSource.getMessage("success.appointment.created", null,
                LocaleContextHolder.getLocale());
        return ResponseEntity.ok(ApiResponse.success(response, message));
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "Cancel an appointment (owner only; release slot to AVAILABLE)")
    public ApiResponse<AppointmentCancelResponse> cancel(@PathVariable("id") Long appointmentId,
            @Valid @RequestBody AppointmentCancelRequest request) {

        log.info("Cancel appointment id={}, confirmPolicy={}, reason={}", appointmentId,
                request.confirmPolicy(), request.reason());

        var resp = appointmentService.cancelByPatient(appointmentId, request.confirmPolicy());
        String message = messageSource.getMessage("success.appointment.cancelled", null,
                LocaleContextHolder.getLocale());
        return ApiResponse.success(resp, message);
    }
}
