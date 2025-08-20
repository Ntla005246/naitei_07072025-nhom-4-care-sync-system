package com.example.backend.service;

import com.example.backend.dto.AppointmentCreateRequest;
import com.example.backend.dto.AppointmentCreateResponse;
import com.example.backend.dto.AppointmentRescheduleRequest;
import com.example.backend.dto.AppointmentRescheduleResponse;

public interface AppointmentService {
    AppointmentCreateResponse create(AppointmentCreateRequest request);

    AppointmentRescheduleResponse reschedule(Long appointmentId,
            AppointmentRescheduleRequest request);
}
