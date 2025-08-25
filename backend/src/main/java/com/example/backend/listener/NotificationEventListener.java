package com.example.backend.listener;

import com.example.backend.event.*;
import com.example.backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;

    @Async
    @EventListener({AppointmentCreatedEvent.class, DoctorNewAppointmentRequestEvent.class})
    public void onEvent(Object event) {
        notificationService.handle(event);
    }
}
