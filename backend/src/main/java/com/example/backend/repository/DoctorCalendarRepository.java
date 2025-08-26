package com.example.backend.repository;

import com.example.backend.entity.DoctorCalendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DoctorCalendarRepository extends JpaRepository<DoctorCalendar, Long> {

    Optional<DoctorCalendar> findByDoctorIdAndActiveTrue(Long doctorId);

    Optional<DoctorCalendar> findByDoctorIdAndCalendarTypeAndActiveTrue(Long doctorId,
            DoctorCalendar.CalendarType calendarType);

    Optional<DoctorCalendar> findByDoctorId(Long doctorId);
}
