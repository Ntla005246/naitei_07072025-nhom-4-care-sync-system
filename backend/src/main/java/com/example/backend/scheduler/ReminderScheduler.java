package com.example.backend.scheduler;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReminderScheduler {

    private final ApplicationEventPublisher publisher;

    // Every 15 minutes
    @Scheduled(cron = "0 */15 * * * *", zone = "Asia/Ho_Chi_Minh")
    public void scheduleReminders() {
        // TODO: Query upcoming appointments in next ~24h and publish events
        // publisher.publishEvent(new AppointmentReminderEvent(...));
    }
}
