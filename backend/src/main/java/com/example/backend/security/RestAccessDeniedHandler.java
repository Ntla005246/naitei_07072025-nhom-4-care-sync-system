package com.example.backend.security;

import com.example.backend.constant.ErrorCodes;
import com.example.backend.dto.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final MessageSource messageSource;
    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");

        String message = messageSource.getMessage("error.access.denied", null,
                LocaleContextHolder.getLocale());
        ApiResponse<Void> body = ApiResponse.error(ErrorCodes.ACCESS_DENIED, message,
                HttpServletResponse.SC_FORBIDDEN);

        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
