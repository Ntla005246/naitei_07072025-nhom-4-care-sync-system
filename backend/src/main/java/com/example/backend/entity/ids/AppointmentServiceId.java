package com.example.backend.entity.ids;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class AppointmentServiceId implements Serializable {

    @Column(name = "appointment_id")
    private Long appointmentId;

    @Column(name = "service_id")
    private Long serviceId;
}
