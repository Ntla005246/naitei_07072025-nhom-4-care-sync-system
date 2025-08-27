package com.example.backend.service.impl;

import com.example.backend.dto.ApiResponse;
import com.example.backend.dto.DoctorTimeOffRequestDto;
import com.example.backend.entity.Doctor;
import com.example.backend.entity.DoctorTimeOff;
import com.example.backend.entity.User;
import com.example.backend.repository.DoctorRepository;
import com.example.backend.repository.DoctorTimeOffRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.DoctorTimeOffService;
import com.example.backend.exception.BusinessException;
import com.example.backend.exception.UnauthorizedException;
import com.example.backend.util.TimeOffValidator;
import com.example.backend.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.AccessDeniedException;
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
    private final UserRepository userRepository;
    private final TimeOffValidator timeOffValidator;
    private final MessageSource messageSource;

    @Override
    public ApiResponse<String> createTimeOff(DoctorTimeOffRequestDto request) {
        // Lấy thông tin doctor từ current user
        String email = SecurityUtils.getCurrentUserEmailOrThrow();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("error.user.not.found"));

        Doctor doctor = currentUser.getDoctor();
        if (doctor == null) {
            throw new AccessDeniedException("error.access.denied");
        }

        log.info("Creating time off for doctor: {}", doctor.getId());

        // Parse và validate datetime
        LocalDateTime startDateTime = timeOffValidator.parseDateTime(request.startDatetime());
        LocalDateTime endDateTime = timeOffValidator.parseDateTime(request.endDatetime());

        // Validation business logic
        timeOffValidator.validateTimeOffRequest(startDateTime, endDateTime);

        // Kiểm tra xung đột thời gian
        if (doctorTimeOffRepository.existsOverlappingTimeOff(doctor.getId(), startDateTime,
                endDateTime)) {
            throw new BusinessException("error.time.off.conflict");
        }

        // Tạo DoctorTimeOff
        DoctorTimeOff timeOff = new DoctorTimeOff();
        timeOff.setDoctor(doctor);
        timeOff.setStartDatetime(startDateTime);
        timeOff.setEndDatetime(endDateTime);
        timeOff.setReason(request.reason());

        doctorTimeOffRepository.save(timeOff);

        log.info("Time off created successfully for doctor: {}", doctor.getId());
        String message = messageSource.getMessage("success.doctor.time.off.created", null,
                LocaleContextHolder.getLocale());
        return ApiResponse.success(null, message);
    }
}
