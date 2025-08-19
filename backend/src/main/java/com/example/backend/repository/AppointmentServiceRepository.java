package com.example.backend.repository;

import com.example.backend.entity.AppointmentService;
import com.example.backend.entity.ids.AppointmentServiceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface AppointmentServiceRepository
        extends
            JpaRepository<AppointmentService, AppointmentServiceId> {

    @Query("SELECT a FROM AppointmentService a WHERE a.appointment.id = :appointmentId")
    List<AppointmentService> findByAppointmentId(Long appointmentId);
}
