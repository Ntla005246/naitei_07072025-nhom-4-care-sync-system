package com.example.backend.service;

import com.example.backend.dto.DoctorCalendarDto;
import java.io.IOException;

public interface CalendarService {

    /**
     * Get calendar integration for current doctor
     */
    DoctorCalendarDto getMyCalendarIntegration();

    /**
     * Disconnect current doctor's calendar
     */
    void disconnectMyCalendar();

    /**
     * Get Google OAuth2 authorization URL for current doctor
     */
    String getGoogleAuthorizationUrl();

    /**
     * Handle OAuth2 callback from Google
     */
    void handleGoogleOAuthCallback(String code, String state) throws IOException;
}
