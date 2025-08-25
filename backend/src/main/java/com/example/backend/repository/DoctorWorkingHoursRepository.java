package com.example.backend.repository;

import com.example.backend.entity.DoctorWorkingHours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalTime;
import java.util.List;

public interface DoctorWorkingHoursRepository extends JpaRepository<DoctorWorkingHours, Long> {

    List<DoctorWorkingHours> findByDoctorIdOrderByDayOfWeekAscStartTimeAsc(Long doctorId);

    @Query("""
              select (count(w) > 0) from DoctorWorkingHours w
              where w.doctor.id = :doctorId
                and w.dayOfWeek = :dayOfWeek
                and (w.startTime < :endTime and :startTime < w.endTime)
            """)
    boolean existsOverlap(Long doctorId, int dayOfWeek, LocalTime startTime, LocalTime endTime);

    @Query("""
              select (count(w) > 0) from DoctorWorkingHours w
              where w.doctor.id = :doctorId
                and w.dayOfWeek = :dayOfWeek
                and (w.startTime < :endTime and :startTime < w.endTime)
                and w.id <> :excludeId
            """)
    boolean existsOverlapExcludingId(Long doctorId, int dayOfWeek, LocalTime startTime,
            LocalTime endTime, Long excludeId);

}
