package com.example.backend.mapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.backend.dto.DoctorDto;
import com.example.backend.entity.Doctor;

@Component
public class DoctorMapper {
    public Optional<DoctorDto> toDto(Doctor doctor){
        if (doctor == null){
            return Optional.empty();
        }
        return Optional.of(DoctorDto.builder()
                .id(doctor.getId())
                .fullName(doctor.getUser().getFullName())
                .email(doctor.getUser().getEmail())
                .phone(doctor.getUser().getPhoneNumber())
                .title(doctor.getTitle())
                .specialtyName(doctor.getSpecialty().getName())
                .experienceYears(doctor.getExperienceYears())
                .bio(doctor.getBio())
                .consultationFee(doctor.getConsultationFee())
                .build());
    }

    public List<DoctorDto> toDtoList(List<Doctor> doctors){
        if (doctors == null || doctors.isEmpty()){
            return Collections.emptyList();
        }

        return doctors.stream()
                .map(this::toDto)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

}
