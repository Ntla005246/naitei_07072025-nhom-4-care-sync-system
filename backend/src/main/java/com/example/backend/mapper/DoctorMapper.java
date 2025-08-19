package com.example.backend.mapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.backend.dto.DoctorDto;
import com.example.backend.entity.Doctor;
import com.example.backend.entity.Specialty;
import com.example.backend.entity.User;

@Component
public class DoctorMapper {
    public Optional<DoctorDto> toDto(Doctor doctor) {
        if (doctor == null) {
            return Optional.empty();
        }

        User user = doctor.getUser();
        Specialty specialty = doctor.getSpecialty();

        String fullName = (user != null) ? user.getFullName() : null;
        String email = (user != null) ? user.getEmail() : null;
        String phone = (user != null) ? user.getPhoneNumber() : null;
        String specialtyName = (specialty != null) ? specialty.getName() : null;

        return Optional.of(new DoctorDto(doctor.getId(), fullName, email, phone, doctor.getTitle(),
                specialtyName, doctor.getExperienceYears(), doctor.getBio(),
                doctor.getConsultationFee()));
    }

    public List<DoctorDto> toDtoList(List<Doctor> doctors) {
        if (doctors == null || doctors.isEmpty()) {
            return Collections.emptyList();
        }

        return doctors.stream().map(this::toDto).filter(Optional::isPresent).map(Optional::get)
                .collect(Collectors.toList());
    }

}
