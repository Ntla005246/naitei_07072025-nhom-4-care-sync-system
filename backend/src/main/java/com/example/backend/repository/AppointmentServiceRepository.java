package com.example.backend.repository;

import com.example.backend.entity.AppointmentService;
import com.example.backend.entity.ids.AppointmentServiceId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentServiceRepository
        extends
            JpaRepository<AppointmentService, AppointmentServiceId> {
}
