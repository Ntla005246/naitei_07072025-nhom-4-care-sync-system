package com.example.backend.controller;

import com.example.backend.constant.ApiConstants;
import com.example.backend.dto.ApiResponse;
import com.example.backend.dto.PageResponse;
import com.example.backend.dto.SpecialtyDto;
import com.example.backend.dto.SpecialtySearchRequest;
import com.example.backend.service.SpecialtyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping(ApiConstants.SPECIALTIES_ENDPOINT)
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Specialty", description = "Specialty management APIs")
public class SpecialtyController {

    private final SpecialtyService specialtyService;
    private final MessageSource messageSource;

    @GetMapping("/search")
    @Operation(summary = "Search specialties with filtering and pagination", description = "Search specialties by name, filter by location and status, with pagination support")
    public ApiResponse<PageResponse<SpecialtyDto>> searchSpecialties(
            @Valid @ModelAttribute SpecialtySearchRequest request) {

        log.info("Received specialty search request: q={}, location={}, page={}, size={}",
                request.getQ(), request.getLocation(), request.getPage(), request.getSize());

        PageResponse<SpecialtyDto> result = specialtyService.searchSpecialties(request);
        String message = messageSource.getMessage("success.specialty.search", null,
                LocaleContextHolder.getLocale());

        return ApiResponse.success(result, message);
    }
}
