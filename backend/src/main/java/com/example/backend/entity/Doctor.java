package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "doctors")
public class Doctor extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", unique = true)
    private User user;

    // ManyToOne
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialty_id")
    private Specialty specialty;

    private String title;

    @Column(name = "experience_years")
    private Integer experienceYears;

    @Column(columnDefinition = "text")
    private String bio;

    @Column(name = "consultation_fee")
    private BigDecimal consultationFee;

    // Association
    @OneToMany(mappedBy = "doctor")
    private Set<DoctorWorkingHours> workingHours;

    @OneToMany(mappedBy = "doctor")
    private Set<DoctorTimeOff> timeOffs;

    @OneToMany(mappedBy = "doctor")
    private Set<AppointmentSlot> appointmentSlots;
}
