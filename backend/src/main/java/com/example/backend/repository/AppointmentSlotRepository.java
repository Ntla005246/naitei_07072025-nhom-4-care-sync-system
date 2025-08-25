package com.example.backend.repository;

import com.example.backend.entity.AppointmentSlot;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface AppointmentSlotRepository extends JpaRepository<AppointmentSlot, Long> {

    // CAS: Đặt slot nếu còn AVAILABLE, link appointment_id
    @Modifying
    @Query(value = """
            UPDATE appointment_slots
            SET status = 'BOOKED', appointment_id = :appointmentId
            WHERE id = :slotId AND status = 'AVAILABLE'
            """, nativeQuery = true)
    int reserveSlot(@Param("slotId") Long slotId, @Param("appointmentId") Long appointmentId);

    // CAS: chỉ free slot nếu slot đang gắn appointment_id đúng và vẫn còn tương lai
    @Modifying
    @Query(value = """
            UPDATE appointment_slots
            SET status = 'AVAILABLE', appointment_id = NULL
            WHERE appointment_id = :appointmentId
              AND start_time > NOW()
            """, nativeQuery = true)
    int freeSlotByAppointmentId(@Param("appointmentId") Long appointmentId);

    @Query("""
            SELECT s FROM AppointmentSlot s
            WHERE s.appointment.id = :appointmentId
            """)
    AppointmentSlot findByAppointmentId(@Param("appointmentId") Long appointmentId);

    // CAS: Kiểm tra có slot nào của bác sĩ bị giao nhau với [start, end) không
    @Query("""
            SELECT case when count(s)>0 then true else false end
            FROM AppointmentSlot s
            WHERE s.doctor.id = :doctorId
              and s.startTime < :end
              and s.endTime   > :start
            """)
    boolean existsOverlap(@Param("doctorId") Long doctorId, @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}
