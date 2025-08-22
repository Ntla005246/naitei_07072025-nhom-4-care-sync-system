package com.example.backend.util;

import com.example.backend.constant.enums.NotificationType;
import com.example.backend.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

@Slf4j
public class NotificationValidationUtil {

    /**
     * Validates notification parameters and throws BusinessException if invalid
     */
    public static void validateNotificationParams(NotificationType type, String recipientEmail,
            String template, String subjectKey, String contentKey) {

        if (type == null) {
            log.error("Notification type cannot be null");
            throw new BusinessException("error.notification.type.required");
        }

        if (!StringUtils.hasText(recipientEmail)) {
            log.error("Recipient email cannot be null or empty for notification type: {}", type);
            throw new BusinessException("error.notification.email.required");
        }

        if (!StringUtils.hasText(template)) {
            log.error("Email template cannot be null or empty for notification type: {}", type);
            throw new BusinessException("error.notification.template.required");
        }

        if (!StringUtils.hasText(subjectKey)) {
            log.error("Subject key cannot be null or empty for notification type: {}", type);
            throw new BusinessException("error.notification.subject.required");
        }

        if (!StringUtils.hasText(contentKey)) {
            log.error("Content key cannot be null or empty for notification type: {}", type);
            throw new BusinessException("error.notification.content.required");
        }
    }

    /**
     * Validates that recipientEmail is not null or empty
     */
    public static void validateEmail(String email, String context) {
        if (StringUtils.hasText(email)) {
            return;
        }
        log.error("Email cannot be null or empty for context: {}", context);
        throw new BusinessException("error.notification.email.required");
    }

    /**
     * Validates that event is not null
     */
    public static void validateEvent(Object event) {
        if (event == null) {
            log.warn("Received null event, ignoring");
            throw new BusinessException("error.notification.event.required");
        }
    }
}
