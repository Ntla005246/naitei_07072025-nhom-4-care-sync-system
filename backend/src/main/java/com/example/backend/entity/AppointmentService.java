package com.example.backend.entity;

import com.example.backend.entity.ids.AppointmentServiceId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "appointment_services")
public class AppointmentService extends BaseEntity {

    @EmbeddedId
    private AppointmentServiceId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("appointmentId")
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("serviceId")
    @JoinColumn(name = "service_id")
    private Service service;

    @Column(name = "price_at_booking", nullable = false)
    private BigDecimal priceAtBooking;
}
