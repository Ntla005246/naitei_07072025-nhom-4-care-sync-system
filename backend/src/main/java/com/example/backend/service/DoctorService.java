package com.example.backend.service;

import java.util.List;

import com.example.backend.dto.DoctorDto;

public interface DoctorService {
    List<DoctorDto> getAllDoctors();
    List<DoctorDto> getAllActiveDoctors();
    List<DoctorDto> getDoctorsBySpecialty(Long specialtyId);
}
