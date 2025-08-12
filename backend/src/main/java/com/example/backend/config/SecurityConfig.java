package com.example.backend.config;

import com.example.backend.constant.ApiConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String[] PUBLIC_ENDPOINTS = {ApiConstants.AUTH_ENDPOINT + "/**",
            "/swagger-ui/**", "/api-docs/**", "/swagger-ui.html", "/actuator/**"};

    private static final String[] PUBLIC_GET_ENDPOINTS = {ApiConstants.DOCTORS_ENDPOINT + "/**",
            "/specialties/**"};

    private static final String[] PATIENT_ENDPOINTS = {ApiConstants.APPOINTMENTS_ENDPOINT + "/**",
            "/payments/**", "/notifications/**", "/feedback/**"};

    private static final String[] DOCTOR_ENDPOINTS = {ApiConstants.DOCTORS_ENDPOINT + "/**"};

    private static final String[] ADMIN_ENDPOINTS = {ApiConstants.ADMIN_ENDPOINT + "/**"};

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authz -> authz.requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                        .requestMatchers(HttpMethod.GET, PUBLIC_GET_ENDPOINTS).permitAll()
                        .requestMatchers(PATIENT_ENDPOINTS).hasAnyRole("PATIENT", "DOCTOR", "ADMIN")
                        .requestMatchers(DOCTOR_ENDPOINTS).hasAnyRole("DOCTOR", "ADMIN")
                        .requestMatchers(ADMIN_ENDPOINTS).hasRole("ADMIN").anyRequest()
                        .authenticated());

        return http.build();
    }
}
