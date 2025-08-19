package com.example.backend.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.dto.AppointmentCreateRequest;
import com.example.backend.dto.AppointmentCreateResponse;
import com.example.backend.entity.Appointment;
import com.example.backend.entity.AppointmentSlot;
import com.example.backend.entity.Patient;
import com.example.backend.entity.ids.AppointmentServiceId;
import com.example.backend.exception.BusinessException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.AppointmentRepository;
import com.example.backend.repository.AppointmentServiceRepository;
import com.example.backend.repository.AppointmentSlotRepository;
import com.example.backend.repository.PatientRepository;
import com.example.backend.repository.ServiceRepository;
import com.example.backend.service.AppointmentService;
import com.example.backend.dto.AppointmentRejectRequest;
import com.example.backend.constant.enums.AppointmentStatus;
import com.example.backend.mapper.AppointmentMapper;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentSlotRepository appointmentSlotRepository;
    private final AppointmentServiceRepository appointmentServiceRepository;
    private final ServiceRepository serviceRepository;
    private final PatientRepository patientRepository;

    @Override
    @Transactional
    public AppointmentCreateResponse create(AppointmentCreateRequest request) {
        log.info("Creating appointment for patient {} with slot {}", request.patientId(),
                request.slotId());

        AppointmentSlot slot = appointmentSlotRepository.findById(request.slotId()).orElseThrow(
                () -> new ResourceNotFoundException("error.slot.not.found", request.slotId()));

        if (slot.getStatus() != com.example.backend.constant.enums.AppointmentSlotStatus.AVAILABLE) {
            throw new BusinessException("error.appointment.slot.unavailable");
        }
        if (slot.getStartTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("error.appointment.slot.unavailable");
        }

        Patient patient = patientRepository.findById(request.patientId())
                .orElseThrow(() -> new ResourceNotFoundException("error.patient.not.found",
                        request.patientId()));

        // Load services
        List<com.example.backend.entity.Service> services = serviceRepository
                .findAllById(request.serviceIds());
        if (services.size() != request.serviceIds().size()) {
            throw new ResourceNotFoundException("error.service.not.found");
        }

        // Validate specialty match
        Long slotSpecialtyId = slot.getDoctor().getSpecialty() != null
                ? slot.getDoctor().getSpecialty().getId().longValue()
                : null;
        boolean allMatch = services.stream()
                .allMatch(svc -> svc.getSpecialty() != null && java.util.Objects
                        .equals(svc.getSpecialty().getId().longValue(), slotSpecialtyId));
        if (!allMatch) {
            throw new BusinessException("error.service.specialty.mismatch");
        }

        // Create Appointment (PENDING)
        Appointment appointmentToSave = new Appointment();
        appointmentToSave.setPatient(patient);
        appointmentToSave.setNotes(request.notes());
        appointmentToSave.setStatus(com.example.backend.constant.enums.AppointmentStatus.PENDING);
        final Appointment savedAppt = appointmentRepository.save(appointmentToSave);

        // Reserve slot via CAS
        int updated = appointmentSlotRepository.reserveSlot(slot.getId(), savedAppt.getId());
        if (updated == 0) {
            throw new BusinessException("error.appointment.slot.unavailable");
        }

        // Build AppointmentService rows and persist priceAtBooking
        List<com.example.backend.entity.AppointmentService> appointmentServices = services.stream()
                .map(svc -> {
                    var as = new com.example.backend.entity.AppointmentService();
                    as.setId(new AppointmentServiceId(savedAppt.getId(), svc.getId()));
                    as.setAppointment(savedAppt);
                    as.setService(svc);
                    as.setPriceAtBooking(svc.getPrice());
                    return as;
                }).toList();
        appointmentServiceRepository.saveAll(appointmentServices);

        BigDecimal total = appointmentServices.stream()
                .map(com.example.backend.entity.AppointmentService::getPriceAtBooking)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return AppointmentMapper.buildFromServices(savedAppt, patient, slot, services, total,
                request.notes());
    }
    
    @Override
    @Transactional
    public AppointmentCreateResponse confirm(Long appointmentId) {
        log.info("Confirm appointment: {}", appointmentId);
        var appt = appointmentRepository.findById(appointmentId).orElseThrow(
                () -> new ResourceNotFoundException("error.appointment.not.found", appointmentId));

        if (appt.getStatus() != AppointmentStatus.PENDING) {
            throw new BusinessException("error.appointment.invalid.state", appt.getStatus().name());
        }

        var slot = appointmentSlotRepository.findByAppointmentId(appointmentId);
        if (slot == null) {
            throw new BusinessException("error.appointment.slot.missing");
        }
        if (slot.getStartTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("error.appointment.cannot.confirm.after.start");
        }

        appt.setStatus(AppointmentStatus.CONFIRMED);
        var saved = appointmentRepository.save(appt);

        var apptServices = appointmentServiceRepository.findByAppointmentId(saved.getId());
        return AppointmentMapper.buildFromAppointmentServices(saved, slot, apptServices);
    }

    @Override
    @Transactional
    public AppointmentCreateResponse reject(Long appointmentId, AppointmentRejectRequest request) {
        log.info("Reject appointment: {}, reason: {}", appointmentId, request.reason());
        var appt = appointmentRepository.findById(appointmentId).orElseThrow(
                () -> new ResourceNotFoundException("error.appointment.not.found", appointmentId));

        if (appt.getStatus() != AppointmentStatus.PENDING
                && appt.getStatus() != AppointmentStatus.CONFIRMED) {
            throw new BusinessException("error.appointment.invalid.state", appt.getStatus().name());
        }

        var slot = appointmentSlotRepository.findByAppointmentId(appointmentId);
        if (slot == null) {
            throw new BusinessException("error.appointment.slot.missing");
        }

        // policy: không cho hủy sau giờ bắt đầu
        if (slot.getStartTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("error.appointment.cannot.reject.after.start");
        }

        appt.setStatus(AppointmentStatus.REJECTED);
        var saved = appointmentRepository.save(appt);

        // free slot (CAS)
        appointmentSlotRepository.freeSlotByAppointmentId(saved.getId());

        var apptServices = appointmentServiceRepository.findByAppointmentId(saved.getId());
        return AppointmentMapper.buildFromAppointmentServices(saved, slot, apptServices);
    }
}
