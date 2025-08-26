package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "doctor_calendars")
public class DoctorCalendar extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false, unique = true)
    private Doctor doctor;

    @Column(name = "calendar_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private CalendarType calendarType;

    @Column(name = "calendar_id")
    private String calendarId;

    @Column(name = "access_token", columnDefinition = "TEXT")
    private String accessToken;

    @Column(name = "refresh_token", columnDefinition = "TEXT")
    private String refreshToken;

    @Column(name = "token_expiry")
    private LocalDateTime tokenExpiry;

    @Column(name = "is_active")
    private boolean active = true;

    @Column(name = "last_event_id")
    private String lastEventId;

    public enum CalendarType {
        GOOGLE, OUTLOOK
    }
}
