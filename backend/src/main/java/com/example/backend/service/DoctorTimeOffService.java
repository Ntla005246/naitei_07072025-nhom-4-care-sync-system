package com.example.backend.service;

import com.example.backend.dto.ApiResponse;
import com.example.backend.dto.DoctorTimeOffRequestDto;

public interface DoctorTimeOffService {
    ApiResponse<String> createTimeOff(DoctorTimeOffRequestDto request);
}
