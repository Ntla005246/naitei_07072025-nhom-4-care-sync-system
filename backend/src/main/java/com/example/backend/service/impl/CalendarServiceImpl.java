package com.example.backend.service.impl;

import com.example.backend.dto.DoctorCalendarDto;
import com.example.backend.entity.Doctor;
import com.example.backend.entity.DoctorCalendar;
import com.example.backend.entity.User;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.exception.UnauthorizedException;
import com.example.backend.repository.DoctorCalendarRepository;
import com.example.backend.repository.DoctorRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.CalendarService;
import com.example.backend.service.GoogleOAuthService;
import com.example.backend.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalendarServiceImpl implements CalendarService {

    private final DoctorCalendarRepository doctorCalendarRepository;
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final GoogleOAuthService googleOAuthService;

    @Override
    @Transactional(readOnly = true)
    public DoctorCalendarDto getMyCalendarIntegration() {
        Doctor doctor = getCurrentDoctor();
        log.info("Getting calendar integration for doctor: {}", doctor.getId());

        DoctorCalendar calendar = doctorCalendarRepository
                .findByDoctorIdAndActiveTrue(doctor.getId())
                .orElseThrow(() -> new ResourceNotFoundException("error.calendar.not.found",
                        doctor.getId()));

        return DoctorCalendarDto.fromEntity(calendar);
    }

    @Override
    @Transactional
    public void disconnectMyCalendar() {
        Doctor doctor = getCurrentDoctor();
        log.info("Disconnecting calendar for doctor: {}", doctor.getId());

        DoctorCalendar calendar = doctorCalendarRepository
                .findByDoctorIdAndActiveTrue(doctor.getId())
                .orElseThrow(() -> new ResourceNotFoundException("error.calendar.not.found",
                        doctor.getId()));

        calendar.setActive(false);
        doctorCalendarRepository.save(calendar);
    }

    @Override
    public String getGoogleAuthorizationUrl() {
        Doctor doctor = getCurrentDoctor();
        log.info("Starting Google OAuth2 flow for doctor: {}", doctor.getId());

        return googleOAuthService.getAuthorizationUrl(doctor.getId());
    }

    @Override
    @Transactional
    public void handleGoogleOAuthCallback(String code, String state) throws IOException {
        log.info("Handling OAuth2 callback for state: {}", state);

        Long doctorId = Long.parseLong(state);
        DoctorCalendar oauthCalendar = googleOAuthService.handleCallback(code, state);

        // Upsert record by doctorId (including inactive)
        DoctorCalendar calendar = doctorCalendarRepository.findByDoctorId(doctorId)
                .orElseGet(DoctorCalendar::new);
        calendar.setDoctor(doctorRepository.getReferenceById(doctorId));
        calendar.setCalendarType(oauthCalendar.getCalendarType());
        calendar.setCalendarId(oauthCalendar.getCalendarId());
        calendar.setAccessToken(oauthCalendar.getAccessToken());
        calendar.setRefreshToken(oauthCalendar.getRefreshToken());
        calendar.setTokenExpiry(oauthCalendar.getTokenExpiry());
        calendar.setActive(true);

        doctorCalendarRepository.save(calendar);
    }

    private Doctor getCurrentDoctor() {
        String currentUserEmail = SecurityUtils.getCurrentUserEmailOrThrow();
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new UnauthorizedException("error.user.not.found"));

        Doctor doctor = currentUser.getDoctor();
        if (doctor == null) {
            throw new AccessDeniedException("error.access.denied");
        }

        return doctor;
    }
}
