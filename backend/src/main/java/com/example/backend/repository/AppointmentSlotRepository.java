package com.example.backend.repository;

import com.example.backend.entity.AppointmentSlot;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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

    // Hủy liên kết slot cũ với appointment nếu slot hiện đang thuộc về appointment
    // đó
    @Modifying
    @Query(value = """
            UPDATE appointment_slots
            SET status = 'AVAILABLE', appointment_id = NULL
            WHERE id = :slotId AND appointment_id = :appointmentId
            """, nativeQuery = true)
    int releaseSlot(@Param("slotId") Long slotId, @Param("appointmentId") Long appointmentId);
}
