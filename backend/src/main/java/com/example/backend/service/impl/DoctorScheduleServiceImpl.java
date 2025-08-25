package com.example.backend.service.impl;

import com.example.backend.dto.ScheduleTemplateDto;
import com.example.backend.dto.ScheduleTemplateUpsertRequest;
import com.example.backend.entity.Doctor;
import com.example.backend.entity.DoctorWorkingHours;
import com.example.backend.exception.BusinessException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.mapper.DoctorScheduleMapper;
import com.example.backend.repository.DoctorRepository;
import com.example.backend.repository.DoctorWorkingHoursRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.DoctorScheduleService;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
public class DoctorScheduleServiceImpl implements DoctorScheduleService {

    private final DoctorRepository doctorRepository;
    private final DoctorWorkingHoursRepository workingHoursRepository;
    private final UserRepository userRepository;

    public DoctorScheduleServiceImpl(DoctorRepository doctorRepository,
            DoctorWorkingHoursRepository workingHoursRepository, UserRepository userRepository) {
        this.doctorRepository = doctorRepository;
        this.workingHoursRepository = workingHoursRepository;
        this.userRepository = userRepository;
    }

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

    private Doctor getCurrentDoctorOrThrow() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null)
            throw new AccessDeniedException("Unauthenticated");

        var user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new AccessDeniedException("User not found"));

        return doctorRepository.findByUserId(user.getId())
                .orElseThrow(() -> new AccessDeniedException("Current user is not a doctor"));
    }

    private void validateTime(LocalTime start, LocalTime end) {
        if (start == null || end == null || !start.isBefore(end))
            throw new BusinessException("error.workinghour.invalid");
    }

    private boolean isShrinking(LocalTime oldStart, LocalTime oldEnd, LocalTime newStart,
            LocalTime newEnd) {
        return newStart.isAfter(oldStart) || newEnd.isBefore(oldEnd);
    }
}
