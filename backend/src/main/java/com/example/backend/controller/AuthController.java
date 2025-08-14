package com.example.backend.controller;

import com.example.backend.constant.ApiConstants;
import com.example.backend.dto.request.RegisterRequest;
import com.example.backend.entity.User;
import com.example.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
