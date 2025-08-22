package com.example.backend.controller;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.constant.ApiConstants;
import com.example.backend.dto.ApiResponse;
import com.example.backend.dto.UserProfileDto;
import com.example.backend.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "User Management", description = "APIs for getting user information")
@RestController
@RequestMapping(ApiConstants.USERS_ENDPOINT)
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    private final UserService userService;
    private final MessageSource messageSource;

    @Operation(summary = "Get current user profile", description = "Get profile information of the currently authenticated user")
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileDto>> getCurrentUser() {
        UserProfileDto userProfileDto = userService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success(userProfileDto, messageSource.getMessage(
                "success.user.profile.retrieved", null, LocaleContextHolder.getLocale())));
    }
}
