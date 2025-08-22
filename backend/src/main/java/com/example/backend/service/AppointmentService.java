package com.example.backend.service;

import com.example.backend.dto.AppointmentCreateRequest;
import com.example.backend.dto.AppointmentCreateResponse;
import com.example.backend.dto.AppointmentFilterRequest;
import com.example.backend.dto.AppointmentListRequest;
import com.example.backend.dto.AppointmentRejectRequest;
import com.example.backend.dto.AppointmentSummaryDto;
import com.example.backend.dto.AppointmentSummaryResponse;
import com.example.backend.dto.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AppointmentService {
    AppointmentCreateResponse create(AppointmentCreateRequest request);
    AppointmentCreateResponse confirm(Long appointmentId);
    AppointmentCreateResponse reject(Long appointmentId, AppointmentRejectRequest request);
    Page<AppointmentSummaryResponse> getMyAppointments(AppointmentFilterRequest filters,
            Pageable pageable);
    PageResponse<AppointmentSummaryDto> listDoctorAppointments(AppointmentListRequest request);
}
