package com.example.backend.service;

import com.example.backend.dto.AppointmentCreateRequest;
import com.example.backend.dto.AppointmentCreateResponse;

public interface AppointmentService {
    AppointmentCreateResponse create(AppointmentCreateRequest request);
}
