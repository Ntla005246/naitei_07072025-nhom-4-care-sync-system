package com.example.backend.service.impl;

import com.example.backend.constant.enums.NotificationStatus;
import com.example.backend.constant.enums.NotificationType;
import com.example.backend.entity.Notification;
import com.example.backend.entity.User;
import com.example.backend.event.AppointmentCreatedEvent;
import com.example.backend.event.DoctorNewAppointmentRequestEvent;
import com.example.backend.exception.BusinessException;
import com.example.backend.repository.NotificationRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.NotificationService;
import com.example.backend.service.EmailService;
import com.example.backend.util.NotificationValidationUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import org.springframework.scheduling.annotation.Async;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final MessageSource messageSource;

    private record HandlerConfig(NotificationType type, String contentKey, String subjectKey,
            String template, Function<Object, Object[]> contentArgs,
            Function<Object, String> recipientEmail,
            Function<Object, Map<String, Object>> ctxVars) {
    }

    private final Map<Class<?>, HandlerConfig> registry = buildRegistry();

    private Map<Class<?>, HandlerConfig> buildRegistry() {
        Map<Class<?>, HandlerConfig> map = new HashMap<>();

        map.put(AppointmentCreatedEvent.class,
                new HandlerConfig(NotificationType.APPOINTMENT_CREATED,
                        "notification.appointment.created", "email.subject.appointment.created",
                        "email/patient/appointment-created",
                        e -> new Object[]{((AppointmentCreatedEvent) e).appointmentId()},
                        e -> ((AppointmentCreatedEvent) e).patientEmail(), e -> {
                            Map<String, Object> vars = new HashMap<>();
                            vars.put("patientName", ((AppointmentCreatedEvent) e).patientName());
                            vars.put("doctorName", ((AppointmentCreatedEvent) e).doctorName());
                            vars.put("specialtyName",
                                    ((AppointmentCreatedEvent) e).specialtyName());
                            vars.put("startTime", ((AppointmentCreatedEvent) e).startTime());
                            vars.put("endTime", ((AppointmentCreatedEvent) e).endTime());
                            vars.put("totalPrice", ((AppointmentCreatedEvent) e).totalPrice());
                            vars.put("appointmentId",
                                    ((AppointmentCreatedEvent) e).appointmentId());
                            // URLs
                            vars.put("appointmentDetailUrl",
                                    com.example.backend.constant.EmailConstants
                                            .appointmentDetailUrl(
                                                    ((AppointmentCreatedEvent) e).appointmentId()));
                            return vars;
                        }));

        map.put(DoctorNewAppointmentRequestEvent.class,
                new HandlerConfig(NotificationType.DOCTOR_NEW_APPOINTMENT_REQUEST,
                        "notification.doctor.new.appointment.request",
                        "email.subject.doctor.new.appointment.request",
                        "email/doctor/new-appointment-request",
                        e -> new Object[]{((DoctorNewAppointmentRequestEvent) e).appointmentId()},
                        e -> ((DoctorNewAppointmentRequestEvent) e).doctorEmail(), e -> {
                            Map<String, Object> vars = new HashMap<>();
                            vars.put("doctorName",
                                    ((DoctorNewAppointmentRequestEvent) e).doctorName());
                            vars.put("patientName",
                                    ((DoctorNewAppointmentRequestEvent) e).patientName());
                            vars.put("patientEmail",
                                    ((DoctorNewAppointmentRequestEvent) e).patientEmail());
                            vars.put("patientPhone",
                                    ((DoctorNewAppointmentRequestEvent) e).patientPhone());
                            vars.put("specialtyName",
                                    ((DoctorNewAppointmentRequestEvent) e).specialtyName());
                            vars.put("serviceNames",
                                    ((DoctorNewAppointmentRequestEvent) e).serviceNames());
                            vars.put("totalPrice",
                                    ((DoctorNewAppointmentRequestEvent) e).totalPrice());
                            vars.put("patientNotes",
                                    ((DoctorNewAppointmentRequestEvent) e).patientNotes());
                            vars.put("appointmentCreatedAt",
                                    ((DoctorNewAppointmentRequestEvent) e).appointmentCreatedAt());
                            vars.put("appointmentStartTime",
                                    ((DoctorNewAppointmentRequestEvent) e).appointmentStartTime());
                            vars.put("appointmentEndTime",
                                    ((DoctorNewAppointmentRequestEvent) e).appointmentEndTime());
                            vars.put("appointmentId",
                                    ((DoctorNewAppointmentRequestEvent) e).appointmentId());
                            // URLs
                            vars.put("appointmentDetailUrl",
                                    com.example.backend.constant.EmailConstants
                                            .doctorAppointmentDetailUrl(
                                                    ((DoctorNewAppointmentRequestEvent) e)
                                                            .appointmentId()));
                            vars.put("confirmAppointmentUrl",
                                    com.example.backend.constant.EmailConstants
                                            .doctorConfirmAppointmentUrl(
                                                    ((DoctorNewAppointmentRequestEvent) e)
                                                            .appointmentId()));
                            vars.put("rejectAppointmentUrl",
                                    com.example.backend.constant.EmailConstants
                                            .doctorRejectAppointmentUrl(
                                                    ((DoctorNewAppointmentRequestEvent) e)
                                                            .appointmentId()));
                            return vars;
                        }));

        return map;
    }

    private String getMessage(String code, Object... args) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(code, args, locale);
    }

    private void notifyPatient(NotificationType type, String contentKey, Object[] contentArgs,
            String subjectKey, String template, String recipientEmail,
            Map<String, Object> ctxVars) {

        // Validate required parameters using utility
        NotificationValidationUtil.validateNotificationParams(type, recipientEmail, template,
                subjectKey, contentKey);

        try {
            // Find user by email - require user to exist
            User user = userRepository.findByEmail(recipientEmail).orElseThrow(() -> {
                log.error("User not found for email: {} when sending notification type: {}",
                        recipientEmail, type);
                return new BusinessException(
                        getMessage("error.notification.user.not.found", recipientEmail));
            });

            // Create and save notification
            Notification notification = createNotification(type, contentKey, contentArgs, user);
            log.info("Notification saved successfully for user: {}, type: {}, content: {}",
                    user.getId(), type, notification.getContent());

            // Send email
            sendNotificationEmail(recipientEmail, subjectKey, template, ctxVars, type);

        } catch (BusinessException e) {
            // Re-throw business exceptions
            throw e;
        } catch (Exception e) {
            log.error("Failed to send notification for type: {}, recipient: {}, error: {}", type,
                    recipientEmail, e.getMessage(), e);
            throw new BusinessException(
                    getMessage("error.notification.send.failed", e.getMessage()), e);
        }
    }

    private Notification createNotification(NotificationType type, String contentKey,
            Object[] contentArgs, User user) {
        Notification notification = new Notification();
        notification.setType(type);
        notification.setStatus(NotificationStatus.UNREAD);
        notification.setContent(getMessage(contentKey, contentArgs));
        notification.setUser(user);
        return notificationRepository.save(notification);
    }

    @Async
    private void sendNotificationEmail(String recipientEmail, String subjectKey, String template,
            Map<String, Object> ctxVars, NotificationType type) {
        Context ctx = new Context(LocaleContextHolder.getLocale());
        if (ctxVars != null && !ctxVars.isEmpty()) {
            ctx.setVariables(ctxVars);
        }

        emailService.sendHtmlEmail(recipientEmail, getMessage(subjectKey), template, ctx);
        log.info("Email sent successfully to: {} for notification type: {}", recipientEmail, type);
    }

    @Override
    public void handle(Object event) {
        try {
            NotificationValidationUtil.validateEvent(event);
        } catch (BusinessException e) {
            log.warn("Invalid event received: {}", e.getMessage());
            return;
        }

        HandlerConfig config = registry.get(event.getClass());
        if (config == null) {
            log.warn("No handler configured for event type: {}", event.getClass().getSimpleName());
            return; // Unknown event, ignore
        }

        try {
            notifyPatient(config.type(), config.contentKey(), config.contentArgs().apply(event),
                    config.subjectKey(), config.template(), config.recipientEmail().apply(event),
                    config.ctxVars().apply(event));
        } catch (Exception e) {
            log.error("Failed to handle event: {}, error: {}", event.getClass().getSimpleName(),
                    e.getMessage(), e);
            // Don't re-throw to prevent event processing from failing the main business
            // flow
        }
    }
}
