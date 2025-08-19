package com.example.backend.controller;

import java.util.List;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.constant.ApiConstants;
import com.example.backend.dto.ApiResponse;
import com.example.backend.dto.DoctorDto;
import com.example.backend.dto.DoctorSearchRequest;
import com.example.backend.dto.PageResponse;
import com.example.backend.service.DoctorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiConstants.DOCTORS_ENDPOINT)
@RequiredArgsConstructor
@Validated
@Tag(name = "Doctor", description = "Doctor management APIs")
public class DoctorController {

    private final DoctorService doctorService;
    private final MessageSource messageSource;

    @GetMapping
    @Operation(summary = "Get all doctors", description = "Retrieve a list of all active doctors")
    public ApiResponse<List<DoctorDto>> getAllDoctors() {
        List<DoctorDto> doctors = doctorService.getAllDoctors();
        String message = messageSource.getMessage("success.doctors.retrieved", null,
                LocaleContextHolder.getLocale());
        return ApiResponse.success(doctors, message);
    }

    @GetMapping("/specialty/{specialtyId}")
    @Operation(summary = "Get doctors by specialty", description = "Retrieve doctors filtered by specialty ID")
    public ApiResponse<List<DoctorDto>> getDoctorsBySpecialty(@PathVariable Long specialtyId) {
        List<DoctorDto> doctors = doctorService.getDoctorsBySpecialty(specialtyId);
        String message = messageSource.getMessage("success.doctors.by.specialty.retrieved", null,
                LocaleContextHolder.getLocale());
        return ApiResponse.success(doctors, message);
    }

    @GetMapping("/search")
    @Operation(summary = "Search doctors with pagination", description = "Search and filter doctors with pagination support")
    public ApiResponse<PageResponse<DoctorDto>> searchDoctors(
            @ModelAttribute DoctorSearchRequest request) {
        PageResponse<DoctorDto> result = doctorService.searchDoctors(request);
        String message = messageSource.getMessage("success.operation", null,
                LocaleContextHolder.getLocale());
        return ApiResponse.success(result, message);
    }
}
