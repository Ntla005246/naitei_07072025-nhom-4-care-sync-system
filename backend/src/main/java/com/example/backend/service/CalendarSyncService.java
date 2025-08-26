package com.example.backend.service;

import com.example.backend.entity.Appointment;
import com.example.backend.entity.AppointmentSlot;
import com.example.backend.constant.enums.AppointmentStatus;
import com.example.backend.entity.DoctorCalendar;
import com.example.backend.entity.Doctor;
import com.example.backend.entity.Patient;
import com.example.backend.entity.User;
import com.example.backend.repository.DoctorCalendarRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalendarSyncService {

    private final DoctorCalendarRepository doctorCalendarRepository;
    private final GoogleCalendarService googleCalendarService;

    public void syncAppointmentToCalendar(Appointment appointment) {
        try {
            AppointmentSlot slot = appointment.getAppointmentSlot();
            if (slot == null) {
                log.warn("Appointment {} has no slot, cannot sync to calendar",
                        appointment.getId());
                return;
            }

            Doctor doctor = slot.getDoctor();
            Optional<DoctorCalendar> calendarOpt = doctorCalendarRepository
                    .findByDoctorIdAndActiveTrue(doctor.getId());

            if (calendarOpt.isEmpty()) {
                log.info("Doctor {} has no active calendar integration", doctor.getId());
                return;
            }

            DoctorCalendar doctorCalendar = calendarOpt.get();

            // Prepare event details based on appointment status
            Patient patient = appointment.getPatient();
            User patientUser = patient.getUser();
            User doctorUser = doctor.getUser();

            String title = getEventTitle(appointment, patientUser);
            String description = getEventDescription(appointment, patientUser);

            // Sync based on calendar type
            switch (doctorCalendar.getCalendarType()) {
                case GOOGLE :
                    if (appointment.getStatus() == AppointmentStatus.CONFIRMED) {
                        // Create event for confirmed appointment
                        String eventId = googleCalendarService.createAppointmentEvent(
                                doctorCalendar, title, description, slot.getStartTime(),
                                slot.getEndTime());
                        // Save event ID for future deletion
                        doctorCalendar.setLastEventId(eventId);
                        doctorCalendarRepository.save(doctorCalendar);
                    } else if (appointment.getStatus() == AppointmentStatus.REJECTED) {
                        // Delete event for rejected appointment
                        if (doctorCalendar.getLastEventId() != null) {
                            googleCalendarService.deleteAppointmentEvent(doctorCalendar,
                                    doctorCalendar.getLastEventId());
                            doctorCalendar.setLastEventId(null);
                            doctorCalendarRepository.save(doctorCalendar);
                        }
                    }
                    break;
                case OUTLOOK :
                    // TODO: Implement Outlook sync
                    log.info("Outlook calendar sync not implemented yet");
                    break;
                default :
                    log.warn("Unknown calendar type: {}", doctorCalendar.getCalendarType());
            }

            log.info("Successfully synced appointment {} (status: {}) to calendar for doctor {}",
                    appointment.getId(), appointment.getStatus(), doctor.getId());

        } catch (Exception e) {
            log.error("Failed to sync appointment {} to calendar", appointment.getId(), e);
            // Don't throw exception to avoid breaking the main appointment flow
        }
    }

    private String getEventTitle(Appointment appointment, User patientUser) {
        String baseTitle = String.format("Lịch hẹn với %s", patientUser.getFullName());

        switch (appointment.getStatus()) {
            case CONFIRMED :
                return "✅ " + baseTitle;
            case COMPLETED :
                return "✅ " + baseTitle + " (Đã hoàn thành)";
            default :
                return "⏳ " + baseTitle + " (Chờ xác nhận)";
        }
    }

    private String getEventDescription(Appointment appointment, User patientUser) {
        StringBuilder description = new StringBuilder();
        description.append(String.format("Bệnh nhân: %s\n", patientUser.getFullName()));
        description.append(String.format("Số điện thoại: %s\n",
                patientUser.getPhoneNumber() != null ? patientUser.getPhoneNumber() : "N/A"));

        if (appointment.getNotes() != null && !appointment.getNotes().trim().isEmpty()) {
            description.append(String.format("Ghi chú: %s\n", appointment.getNotes()));
        }

        description.append(String.format("Trạng thái: %s", appointment.getStatus()));

        return description.toString();
    }
}
