package com.example.backend.service.impl;

import com.example.backend.dto.AppointmentCreateRequest;
import com.example.backend.dto.AppointmentCreateResponse;
import com.example.backend.dto.AppointmentCreateResponse.DoctorInfo;
import com.example.backend.dto.AppointmentCreateResponse.ServiceItem;
import com.example.backend.dto.AppointmentCreateResponse.SlotInfo;
import com.example.backend.dto.AppointmentRescheduleRequest;
import com.example.backend.dto.AppointmentRescheduleResponse;
import com.example.backend.entity.Appointment;
import com.example.backend.entity.AppointmentSlot;
import com.example.backend.entity.Doctor;
import com.example.backend.entity.Patient;
import com.example.backend.entity.Specialty;
import com.example.backend.entity.User;
import com.example.backend.entity.ids.AppointmentServiceId;
import com.example.backend.exception.BusinessException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.AppointmentRepository;
import com.example.backend.repository.AppointmentServiceRepository;
import com.example.backend.repository.AppointmentSlotRepository;
import com.example.backend.repository.PatientRepository;
import com.example.backend.repository.ServiceRepository;
import com.example.backend.service.AppointmentService;
import com.example.backend.constant.enums.AppointmentSlotStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentSlotRepository appointmentSlotRepository;
    private final AppointmentServiceRepository appointmentServiceRepository;
    private final ServiceRepository serviceRepository;
    private final PatientRepository patientRepository;
    private final MessageSource messageSource;

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
        boolean allMatch = services.stream().allMatch(svc -> svc.getSpecialty() != null
                && Objects.equals(svc.getSpecialty().getId().longValue(), slotSpecialtyId));
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

        // Build AppointmentService rows and total
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

        // Build response
        Doctor doctor = slot.getDoctor();
        Specialty specialty = doctor.getSpecialty();
        User user = doctor.getUser();
        return new AppointmentCreateResponse(savedAppt.getId(), patient.getId(),
                new SlotInfo(slot.getStartTime(), slot.getEndTime(), doctor.getId()),
                new DoctorInfo(doctor.getId(),
                        Optional.ofNullable(user).map(User::getFullName).orElse(null),
                        Optional.ofNullable(specialty).map(s -> s.getId().longValue()).orElse(null),
                        Optional.ofNullable(specialty).map(Specialty::getName).orElse(null)),
                savedAppt.getStatus().name(),
                services.stream()
                        .map(svc -> new ServiceItem(svc.getId(), svc.getName(), svc.getPrice()))
                        .toList(),
                total, request.notes());
    }

    @Override
    @Transactional
    public AppointmentRescheduleResponse reschedule(Long appointmentId,
            AppointmentRescheduleRequest request) {

        if (request.confirmPolicy() == null || !request.confirmPolicy()) {
            throw new BusinessException("error.policy.not.confirmed");
        }

        Appointment appt = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("error.appointment.not.found"));

        switch (appt.getStatus()) {
            case PENDING :
            case CONFIRMED :
                break;
            default :
                throw new BusinessException("error.appointment.status.invalid",
                        appt.getStatus().name());
        }

        if (request.patientId() != null) {
            if (appt.getPatient() == null || appt.getPatient().getId() == null
                    || !appt.getPatient().getId().equals(request.patientId())) {
                throw new BusinessException("error.access.denied");
            }
        }

        AppointmentSlot oldSlot = appt.getAppointmentSlot();
        if (oldSlot == null) {
            throw new BusinessException("error.appointment.slot.missing");
        }

        AppointmentSlot newSlot = appointmentSlotRepository.findById(request.newSlotId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("error.appointment.slot.not.found"));

        if (newSlot.getStatus() != AppointmentSlotStatus.AVAILABLE) {
            throw new BusinessException("error.slot.not.available");
        }

        if (oldSlot.getDoctor() != null && newSlot.getDoctor() != null
                && !oldSlot.getDoctor().getId().equals(newSlot.getDoctor().getId())) {
            throw new BusinessException("error.slot.doctor.mismatch");
        }

        var oldInfo = new AppointmentRescheduleResponse.SlotInfo(oldSlot.getStartTime(),
                oldSlot.getEndTime(),
                oldSlot.getDoctor() != null ? oldSlot.getDoctor().getId() : null);

        int released = appointmentSlotRepository.releaseSlot(oldSlot.getId(), appt.getId());
        if (released != 1) {
            throw new BusinessException("error.appointment.slot.release.failed");
        }

        int reserved = appointmentSlotRepository.reserveSlot(newSlot.getId(), appt.getId());
        if (reserved != 1) {
            throw new BusinessException("error.slot.not.available");
        }

        appt.setAppointmentSlot(newSlot);
        appointmentRepository.save(appt);

        var newInfo = new AppointmentRescheduleResponse.SlotInfo(newSlot.getStartTime(),
                newSlot.getEndTime(),
                newSlot.getDoctor() != null ? newSlot.getDoctor().getId() : null);

        String policyMsg = messageSource.getMessage("policy.appointment.reschedule", null,
                org.springframework.context.i18n.LocaleContextHolder.getLocale());

        // TODO: Gửi thông báo (email/notification)
        boolean notificationQueued = false;

        return new AppointmentRescheduleResponse(appt.getId(), oldInfo, newInfo,
                appt.getStatus().name(), policyMsg, notificationQueued);
    }
}
