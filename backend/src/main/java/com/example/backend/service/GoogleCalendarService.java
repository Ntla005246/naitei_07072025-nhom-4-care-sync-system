package com.example.backend.service;

import com.example.backend.entity.DoctorCalendar;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleCalendarService {

    private final GoogleOAuthService googleOAuthService;

    private static final String APPLICATION_NAME = "Care Sync System";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    @Value("${google.calendar.client-id}")
    private String clientId;

    @Value("${google.calendar.client-secret}")
    private String clientSecret;

    public String createAppointmentEvent(DoctorCalendar doctorCalendar, String title,
            String description, LocalDateTime startTime, LocalDateTime endTime) {
        try {
            // Check if token needs refresh
            if (!googleOAuthService.isTokenValid(doctorCalendar)) {
                log.info("Refreshing Google OAuth2 token for doctor: {}",
                        doctorCalendar.getDoctor().getId());
                googleOAuthService.refreshToken(doctorCalendar);
            }

            Calendar service = getCalendarService(doctorCalendar);

            Event event = new Event().setSummary(title).setDescription(description).setStart(
                    new EventDateTime().setDateTime(new com.google.api.client.util.DateTime(
                            startTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())))
                    .setEnd(new EventDateTime().setDateTime(new com.google.api.client.util.DateTime(
                            endTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())));

            String calendarId = doctorCalendar.getCalendarId() != null
                    ? doctorCalendar.getCalendarId()
                    : "primary";

            Event createdEvent = service.events().insert(calendarId, event).execute();
            log.info("Created Google Calendar event: {}", createdEvent.getId());
            return createdEvent.getId();

        } catch (Exception e) {
            log.error("Failed to create Google Calendar event", e);
            throw new RuntimeException("Failed to sync appointment to Google Calendar", e);
        }
    }

    public void deleteAppointmentEvent(DoctorCalendar doctorCalendar, String eventId) {
        try {
            // Check if token needs refresh
            if (!googleOAuthService.isTokenValid(doctorCalendar)) {
                log.info("Refreshing Google OAuth2 token for doctor: {}",
                        doctorCalendar.getDoctor().getId());
                googleOAuthService.refreshToken(doctorCalendar);
            }

            Calendar service = getCalendarService(doctorCalendar);

            String calendarId = doctorCalendar.getCalendarId() != null
                    ? doctorCalendar.getCalendarId()
                    : "primary";

            service.events().delete(calendarId, eventId).execute();
            log.info("Deleted Google Calendar event: {}", eventId);

        } catch (Exception e) {
            log.error("Failed to delete Google Calendar event: {}", eventId, e);
            throw new RuntimeException("Failed to delete appointment from Google Calendar", e);
        }
    }

    private Calendar getCalendarService(DoctorCalendar doctorCalendar)
            throws GeneralSecurityException, IOException {
        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        GoogleCredential credential = new GoogleCredential.Builder().setTransport(httpTransport)
                .setJsonFactory(JSON_FACTORY).setClientSecrets(clientId, clientSecret).build();

        credential.setAccessToken(doctorCalendar.getAccessToken());
        if (doctorCalendar.getRefreshToken() != null) {
            credential.setRefreshToken(doctorCalendar.getRefreshToken());
        }

        return new Calendar.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME).build();
    }
}
