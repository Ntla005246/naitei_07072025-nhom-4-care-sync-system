package com.example.backend.config;

import com.example.backend.constant.ApiConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
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
    public RoleHierarchy roleHierarchy() {
        // ADMIN > DOCTOR > PATIENT
        String hierarchy = "ROLE_ADMIN > ROLE_DOCTOR \n ROLE_DOCTOR > ROLE_PATIENT";
        return RoleHierarchyImpl.fromHierarchy(hierarchy);
    }

    @Bean
    public static MethodSecurityExpressionHandler methodSecurityExpressionHandler(
            RoleHierarchy roleHierarchy) {
        DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
        handler.setRoleHierarchy(roleHierarchy);
        return handler;
    }

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
                        .requestMatchers(PATIENT_ENDPOINTS).hasRole("PATIENT")
                        .requestMatchers(DOCTOR_ENDPOINTS).hasRole("DOCTOR")
                        .requestMatchers(ADMIN_ENDPOINTS).hasRole("ADMIN").anyRequest()
                        .authenticated());

        return http.build();
    }
}
