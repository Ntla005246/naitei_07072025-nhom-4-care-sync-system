package com.example.backend.service.impl;

import com.example.backend.dto.ApiResponse;
import com.example.backend.dto.DoctorTimeOffRequestDto;
import com.example.backend.entity.Doctor;
import com.example.backend.entity.DoctorTimeOff;
import com.example.backend.repository.DoctorRepository;
import com.example.backend.repository.DoctorTimeOffRepository;
import com.example.backend.service.DoctorTimeOffService;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.exception.BusinessException;
import com.example.backend.constant.MessageConstants;
import com.example.backend.util.TimeOffValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DoctorTimeOffServiceImpl implements DoctorTimeOffService {

    private final DoctorTimeOffRepository doctorTimeOffRepository;
    private final DoctorRepository doctorRepository;
    private final TimeOffValidator timeOffValidator;

    @Override
    public ApiResponse<String> createTimeOff(DoctorTimeOffRequestDto request) {
        log.info("Creating time off for doctor: {}", request.doctorId());

        // Parse và validate datetime
        LocalDateTime startDateTime = timeOffValidator.parseDateTime(request.startDatetime());
        LocalDateTime endDateTime = timeOffValidator.parseDateTime(request.endDatetime());

        // Validation business logic
        timeOffValidator.validateTimeOffRequest(startDateTime, endDateTime);

        // Kiểm tra doctor có tồn tại không
        Doctor doctor = doctorRepository.findById(request.doctorId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Doctor not found with id: " + request.doctorId()));

        // Kiểm tra xung đột thời gian
        if (doctorTimeOffRepository.existsOverlappingTimeOff(request.doctorId(), startDateTime,
                endDateTime)) {
            throw new BusinessException("TIME_OFF_CONFLICT", MessageConstants.TIME_OFF_CONFLICT);
        }

        // Tạo DoctorTimeOff
        DoctorTimeOff timeOff = new DoctorTimeOff();
        timeOff.setDoctor(doctor);
        timeOff.setStartDatetime(startDateTime);
        timeOff.setEndDatetime(endDateTime);
        timeOff.setReason(request.reason());

        doctorTimeOffRepository.save(timeOff);

        log.info("Time off created successfully for doctor: {}", request.doctorId());
        return ApiResponse.success("Time off created",
                MessageConstants.DOCTOR_TIME_OFF_CREATED_SUCCESS);
    }
}
