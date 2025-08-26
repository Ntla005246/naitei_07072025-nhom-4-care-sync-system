package com.example.backend.service.impl;

import com.example.backend.dto.ScheduleTemplateDto;
import com.example.backend.dto.ScheduleTemplateUpsertRequest;
import com.example.backend.entity.Doctor;
import com.example.backend.entity.DoctorWorkingHours;
import com.example.backend.exception.BusinessException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.mapper.DoctorScheduleMapper;
import com.example.backend.repository.AppointmentSlotRepository;
import com.example.backend.repository.DoctorRepository;
import com.example.backend.repository.DoctorWorkingHoursRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.DoctorScheduleService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class DoctorScheduleServiceImpl implements DoctorScheduleService {

    private final DoctorRepository doctorRepository;
    private final DoctorWorkingHoursRepository workingHoursRepository;
    private final UserRepository userRepository;
    private final AppointmentSlotRepository appointmentSlotRepository;
    private final MessageSource messageSource;

    // ----------------------
    // CREATE
    // ----------------------
    @Override
    @Transactional
    public ScheduleTemplateDto createTemplate(ScheduleTemplateUpsertRequest req) {
        validateTime(req.startTime(), req.endTime());

        Doctor doctor = getCurrentDoctorOrThrow();

        boolean overlap = workingHoursRepository.existsOverlap(doctor.getId(),
                req.dayOfWeek().getValue(), req.startTime(), req.endTime());
        if (overlap)
            throw new BusinessException("error.workinghour.overlap");

        DoctorWorkingHours e = new DoctorWorkingHours();
        e.setDoctor(doctor);
        e.setDayOfWeek(req.dayOfWeek().getValue());
        e.setStartTime(req.startTime());
        e.setEndTime(req.endTime());

        DoctorWorkingHours saved = workingHoursRepository.save(e);
        return DoctorScheduleMapper.toDto(saved);
    }

    // ----------------------
    // UPDATE
    // ----------------------
    @Override
    @Transactional
    public ScheduleTemplateDto updateTemplate(Long templateId, ScheduleTemplateUpsertRequest req) {
        validateTime(req.startTime(), req.endTime());

        Doctor doctor = getCurrentDoctorOrThrow();

        DoctorWorkingHours e = workingHoursRepository.findById(templateId)
                .orElseThrow(() -> new ResourceNotFoundException("workinghour.not_found"));

        if (!e.getDoctor().getId().equals(doctor.getId()))
            throw new AccessDeniedException("Not owner of this template");

        boolean overlap = workingHoursRepository.existsOverlapExcludingId(doctor.getId(),
                req.dayOfWeek().getValue(), req.startTime(), req.endTime(), e.getId());
        if (overlap)
            throw new BusinessException("error.workinghour.overlap");

        boolean shrink = isShrinking(e.getStartTime(), e.getEndTime(), req.startTime(),
                req.endTime());

        e.setDayOfWeek(req.dayOfWeek().getValue());
        e.setStartTime(req.startTime());
        e.setEndTime(req.endTime());

        DoctorWorkingHours saved = workingHoursRepository.save(e);

        if (shrink) {
            // TODO: đóng/xoá slot AVAILABLE bị ảnh hưởng; nếu đụng BOOKED → throw
            // BusinessException("error.workinghour.affects.booked")
        }

        return DoctorScheduleMapper.toDto(saved);
    }

    // ----------------------
    // DELETE
    // ----------------------
    @Override
    @Transactional
    public void deleteSchedule(Long scheduleId) {
        Doctor doctor = getCurrentDoctorOrThrow();

        DoctorWorkingHours schedule = workingHoursRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("error.workinghour.not.found"));

        if (!schedule.getDoctor().getId().equals(doctor.getId())) {
            throw new AccessDeniedException("error.workinghour.not.owner");
        }

        // Kiểm tra có slot BOOKED trong tương lai hay không
        boolean hasBooked = appointmentSlotRepository.existsBookedFuture(doctor.getId(),
                schedule.getStartTime().atDate(LocalDate.now()),
                schedule.getEndTime().atDate(LocalDate.now()));
        if (hasBooked) {
            throw new BusinessException("error.workinghour.affects.booked");
        }

        workingHoursRepository.delete(schedule);
    }

    // ----------------------
    // Helpers
    // ----------------------
    private Doctor getCurrentDoctorOrThrow() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new AccessDeniedException("error.unauthenticated");
        }

        var user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new AccessDeniedException("error.user.not.found.by.email"));

        return doctorRepository.findByUserId(user.getId())
                .orElseThrow(() -> new AccessDeniedException("error.user.not.doctor"));
    }

    private void validateTime(LocalTime start, LocalTime end) {
        if (start == null || end == null || !start.isBefore(end)) {
            throw new BusinessException("error.workinghour.invalid");
        }
    }

    private boolean isShrinking(LocalTime oldStart, LocalTime oldEnd, LocalTime newStart,
            LocalTime newEnd) {
        return newStart.isAfter(oldStart) || newEnd.isBefore(oldEnd);
    }
}
