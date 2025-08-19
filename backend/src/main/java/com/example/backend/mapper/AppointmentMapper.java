package com.example.backend.mapper;

import com.example.backend.dto.AppointmentCreateResponse;
import com.example.backend.dto.AppointmentCreateResponse.DoctorInfo;
import com.example.backend.dto.AppointmentCreateResponse.ServiceItem;
import com.example.backend.dto.AppointmentCreateResponse.SlotInfo;
import com.example.backend.entity.Appointment;
import com.example.backend.entity.AppointmentSlot;
import com.example.backend.entity.Doctor;
import com.example.backend.entity.Patient;
import com.example.backend.entity.Specialty;
import com.example.backend.entity.User;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public final class AppointmentMapper {
    private AppointmentMapper() {
    }

    public static AppointmentCreateResponse buildFromServices(Appointment savedAppt,
            Patient patient, AppointmentSlot slot,
            List<com.example.backend.entity.Service> services, BigDecimal total, String notes) {
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
                total, notes);
    }

    public static AppointmentCreateResponse buildFromAppointmentServices(Appointment appt,
            AppointmentSlot slot,
            List<com.example.backend.entity.AppointmentService> apptServices) {
        Doctor doctor = slot.getDoctor();
        Specialty specialty = doctor.getSpecialty();
        User user = doctor.getUser();
        BigDecimal total = apptServices.stream()
                .map(com.example.backend.entity.AppointmentService::getPriceAtBooking)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new AppointmentCreateResponse(appt.getId(), appt.getPatient().getId(),
                new SlotInfo(slot.getStartTime(), slot.getEndTime(), doctor.getId()),
                new DoctorInfo(doctor.getId(),
                        Optional.ofNullable(user).map(User::getFullName).orElse(null),
                        Optional.ofNullable(specialty).map(s -> s.getId().longValue()).orElse(null),
                        Optional.ofNullable(specialty).map(Specialty::getName).orElse(null)),
                appt.getStatus().name(),
                apptServices.stream()
                        .map(as -> new ServiceItem(as.getService().getId(),
                                as.getService().getName(), as.getPriceAtBooking()))
                        .toList(),
                total, appt.getNotes());
    }
}
