package com.example.backend.service;

import com.example.backend.dto.LoginRequestDto;
import com.example.backend.dto.request.RegisterRequest;
import com.example.backend.entity.User;
import com.example.backend.dto.JwtAuthResponseDto;

public interface AuthService {

    User register(RegisterRequest request);

    JwtAuthResponseDto login(LoginRequestDto loginDto);

    void verifyEmail(String token);
}
