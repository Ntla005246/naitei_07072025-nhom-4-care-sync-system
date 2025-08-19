package com.example.backend.controller;

import com.example.backend.constant.ApiConstants;
import com.example.backend.dto.request.RegisterRequest;
import com.example.backend.entity.User;
import com.example.backend.constant.MessageConstants;
import com.example.backend.dto.JwtAuthResponseDto;
import com.example.backend.dto.LoginRequestDto;
import com.example.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Auth", description = "Authentication & Authorization APIs")
@RestController
@RequestMapping(ApiConstants.AUTH_ENDPOINT)
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        User registeredUser = authService.register(registerRequest);
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }

    @Operation(summary = "Verify email", description = "Verify email using the token sent to the user's email")
    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        authService.verifyEmail(token);
        return ResponseEntity.ok(MessageConstants.VERIFY_EMAIL_SUCCESS_HTML);
    }

    @Operation(summary = "Login", description = "Login with email and password, returns a JWT token")
    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponseDto> login(@Valid @RequestBody LoginRequestDto loginDto) {
        JwtAuthResponseDto response = authService.login(loginDto);
        return ResponseEntity.ok(response);
    }
}
