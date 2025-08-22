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
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final MessageSource messageSource;
    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        String message = messageSource.getMessage("error.unauthorized", null,
                LocaleContextHolder.getLocale());
        ApiResponse<Void> body = ApiResponse.error(ErrorCodes.UNAUTHORIZED, message,
                HttpServletResponse.SC_UNAUTHORIZED);

        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
