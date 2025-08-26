package com.example.backend.service;

import com.example.backend.entity.DoctorCalendar;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.TokenResponse;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest;

@Service
@Slf4j
public class GoogleOAuthService {

    private static final List<String> SCOPES = Arrays.asList(
            "https://www.googleapis.com/auth/calendar",
            "https://www.googleapis.com/auth/calendar.events");

    @Value("${google.calendar.client-id}")
    private String clientId;

    @Value("${google.calendar.client-secret}")
    private String clientSecret;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    public String getAuthorizationUrl(Long doctorId) {
        String redirectUri = baseUrl + "/api/v1/calendar/oauth/callback";

        AuthorizationCodeRequestUrl authorizationUrl = new com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl(
                clientId, redirectUri, SCOPES).set("state", doctorId.toString())
                .set("access_type", "offline").set("prompt", "consent");

        return authorizationUrl.build();
    }

    public DoctorCalendar handleCallback(String code, String state) throws IOException {
        Long doctorId = Long.parseLong(state);
        String redirectUri = baseUrl + "/api/v1/calendar/oauth/callback";

        TokenResponse tokenResponse = new com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest(
                new NetHttpTransport(), new GsonFactory(), clientId, clientSecret, code,
                redirectUri).execute();

        // Create DoctorCalendar entity
        DoctorCalendar calendar = new DoctorCalendar();
        calendar.setCalendarType(DoctorCalendar.CalendarType.GOOGLE);
        calendar.setAccessToken(tokenResponse.getAccessToken());
        calendar.setRefreshToken(tokenResponse.getRefreshToken());

        if (tokenResponse.getExpiresInSeconds() != null) {
            calendar.setTokenExpiry(
                    java.time.LocalDateTime.now().plusSeconds(tokenResponse.getExpiresInSeconds()));
        }

        log.info("Successfully obtained Google OAuth tokens for doctor: {}", doctorId);
        return calendar;
    }

    public boolean isTokenValid(DoctorCalendar calendar) {
        if (calendar.getTokenExpiry() == null) {
            return true; // No expiry set, assume valid
        }
        return calendar.getTokenExpiry().isAfter(java.time.LocalDateTime.now());
    }

    public void refreshToken(DoctorCalendar calendar) throws IOException {
        if (calendar.getRefreshToken() == null) {
            throw new IllegalStateException("No refresh token available");
        }

        TokenResponse tokenResponse = new GoogleRefreshTokenRequest(new NetHttpTransport(),
                new GsonFactory(), calendar.getRefreshToken(), clientId, clientSecret).execute();

        calendar.setAccessToken(tokenResponse.getAccessToken());
        if (tokenResponse.getRefreshToken() != null) {
            calendar.setRefreshToken(tokenResponse.getRefreshToken());
        }
        if (tokenResponse.getExpiresInSeconds() != null) {
            calendar.setTokenExpiry(
                    java.time.LocalDateTime.now().plusSeconds(tokenResponse.getExpiresInSeconds()));
        }

        log.info("Successfully refreshed Google OAuth token for doctor: {}",
                calendar.getDoctor().getId());
    }
}
