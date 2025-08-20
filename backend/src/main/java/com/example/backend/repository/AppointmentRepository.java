package com.example.backend.repository;

import com.example.backend.constant.enums.AppointmentStatus;
import com.example.backend.entity.Appointment;
import java.time.LocalDateTime;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppointmentRepository
        extends
            JpaRepository<Appointment, Long>,
            JpaSpecificationExecutor<Appointment> {

    @Query("""
            SELECT a FROM Appointment a
            JOIN a.appointmentSlot s
            WHERE s.doctor.id = :doctorId
              AND (:status IS NULL OR a.status = :status)
              AND (:startFrom IS NULL OR s.startTime >= :startFrom)
              AND (:startTo IS NULL OR s.startTime < :startTo)
            ORDER BY s.startTime DESC
            """)
    Page<Appointment> findByDoctorWithFilters(@Param("doctorId") Long doctorId,
            @Param("status") AppointmentStatus status, @Param("startFrom") LocalDateTime startFrom,
            @Param("startTo") LocalDateTime startTo, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
                SELECT a FROM Appointment a
                JOIN FETCH a.appointmentSlot s
                WHERE a.id = :id
            """)
    Optional<Appointment> findByIdWithSlotForUpdate(@Param("id") Long id);
}
