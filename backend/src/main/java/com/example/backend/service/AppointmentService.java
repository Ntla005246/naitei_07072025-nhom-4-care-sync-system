package com.example.backend.service;

import com.example.backend.dto.AppointmentCreateRequest;
import com.example.backend.dto.AppointmentCreateResponse;
import com.example.backend.dto.*;

public interface AppointmentService {
    AppointmentCreateResponse create(AppointmentCreateRequest request);
    AppointmentCreateResponse confirm(Long appointmentId); // trả về DTO tóm tắt
    AppointmentCreateResponse reject(Long appointmentId, AppointmentRejectRequest request);
}
