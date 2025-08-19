package com.example.backend.controller.doctor;

import com.example.backend.constant.ApiConstants;
import com.example.backend.dto.ApiResponse;
import com.example.backend.dto.DoctorTimeOffRequestDto;
import com.example.backend.service.DoctorTimeOffService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping(ApiConstants.DOCTOR_TIME_OFF_ENDPOINT)
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Doctor Time Off", description = "Doctor time off management APIs")
public class DoctorTimeOffController {

    private final DoctorTimeOffService doctorTimeOffService;

    @PostMapping
    @Operation(summary = "Create doctor time off", description = "Create a new time off request for a doctor")
    public ApiResponse<String> createTimeOff(@Valid @RequestBody DoctorTimeOffRequestDto request) {
        log.info("Received time off creation request for doctor: {}", request.doctorId());
        return doctorTimeOffService.createTimeOff(request);
    }
}
