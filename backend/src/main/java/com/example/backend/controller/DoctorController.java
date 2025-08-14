package com.example.backend.controller;

import com.example.backend.constant.ApiConstants;
import com.example.backend.dto.ApiResponse;
import com.example.backend.dto.DoctorDto;
import com.example.backend.service.DoctorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiConstants.DOCTORS_ENDPOINT)
@RequiredArgsConstructor
@Tag(name = "Doctor", description = "Doctor management APIs")
public class DoctorController {

    private final DoctorService doctorService;

    @GetMapping
    @Operation(summary = "Get all doctors", description = "Retrieve a list of all active doctors")
    public ApiResponse<List<DoctorDto>> getAllDoctors() {
        List<DoctorDto> doctors = doctorService.getAllDoctors();
        return ApiResponse.success(doctors, "Doctors retrieved successfully");
    }

    @GetMapping("/specialty/{specialtyId}")
    @Operation(summary = "Get doctors by specialty", description = "Retrieve doctors filtered by specialty ID")
    public ApiResponse<List<DoctorDto>> getDoctorsBySpecialty(@PathVariable Long specialtyId) {
        List<DoctorDto> doctors = doctorService.getDoctorsBySpecialty(specialtyId);
        return ApiResponse.success(doctors, "Doctors retrieved successfully");
    }
}
