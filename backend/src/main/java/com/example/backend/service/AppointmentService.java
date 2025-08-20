package com.example.backend.service;

import com.example.backend.dto.AppointmentCreateRequest;
import com.example.backend.dto.AppointmentCreateResponse;
import com.example.backend.dto.AppointmentListRequest;
import com.example.backend.dto.AppointmentSummaryDto;
import com.example.backend.dto.PageResponse;
import com.example.backend.dto.AppointmentRejectRequest;

public interface AppointmentService {
    AppointmentCreateResponse create(AppointmentCreateRequest request);
    AppointmentCreateResponse confirm(Long appointmentId); // trả về DTO tóm tắt
    AppointmentCreateResponse reject(Long appointmentId, AppointmentRejectRequest request);
    PageResponse<AppointmentSummaryDto> listMyAppointments(AppointmentListRequest request);
}
