package com.example.backend.service;

import com.example.backend.dto.request.RegisterRequest;
import com.example.backend.entity.User;

public interface AuthService {

    User register(RegisterRequest request);

}
