package com.example.backend.controller;

import com.example.backend.constant.ApiConstants;
import com.example.backend.dto.ApiResponse;
import com.example.backend.dto.DoctorCalendarDto;
import com.example.backend.service.CalendarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiConstants.CALENDAR_ENDPOINT)
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Calendar Integration", description = "Calendar integration management APIs")
public class CalendarController {

    private final CalendarService calendarService;
    private final MessageSource messageSource;

    @GetMapping("/integrations/me")
    @Operation(summary = "Get my calendar integration", description = "Get calendar integration status for current doctor")
    public ApiResponse<DoctorCalendarDto> getMyCalendarIntegration() {
        log.info("[CalendarController] Get my calendar integration");
        DoctorCalendarDto dto = calendarService.getMyCalendarIntegration();
        return ApiResponse.success(dto, "Calendar integration found");
    }

    @DeleteMapping("/integrations/me")
    @Operation(summary = "Disconnect my calendar", description = "Disconnect current doctor's calendar integration")
    public ApiResponse<String> disconnectMyCalendar() {
        log.info("[CalendarController] Disconnect my calendar");
        calendarService.disconnectMyCalendar();

        String message = messageSource.getMessage("success.calendar.disconnected", null,
                LocaleContextHolder.getLocale());
        return ApiResponse.success(null, message);
    }

    @GetMapping("/oauth/google/authorize")
    @Operation(summary = "Start Google OAuth2 flow", description = "Get authorization URL for Google Calendar integration")
    public ApiResponse<String> authorizeGoogle() {
        log.info("[CalendarController] Start Google OAuth2 authorization");
        String authorizationUrl = calendarService.getGoogleAuthorizationUrl();
        return ApiResponse.success(authorizationUrl, "Authorization URL generated");
    }

    @GetMapping("/oauth/callback")
    @Operation(summary = "Handle OAuth2 callback", description = "Handle OAuth2 callback from Google")
    public ApiResponse<String> oauthCallback(@RequestParam String code, @RequestParam String state)
            throws java.io.IOException {
        log.info("[CalendarController] Received OAuth2 callback, state={}", state);

        calendarService.handleGoogleOAuthCallback(code, state);

        String message = messageSource.getMessage("success.calendar.connected", null,
                LocaleContextHolder.getLocale());
        return ApiResponse.success(null, message);
    }
}
