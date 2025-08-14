package com.example.backend.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.backend.dto.DoctorDto;
import com.example.backend.entity.Doctor;
import com.example.backend.mapper.DoctorMapper;
import com.example.backend.repository.DoctorRepository;
import com.example.backend.service.DoctorService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;

    @Override
    public List<DoctorDto> getAllDoctors(){
        List<Doctor> doctors = doctorRepository.findAll();
        return doctorMapper.toDtoList(doctors);
    }

    @Override
    public List<DoctorDto> getAllActiveDoctors(){
        List<Doctor> doctors = doctorRepository.findAllActiveDoctors();
        return doctorMapper.toDtoList(doctors);
    }

    @Override
    public List<DoctorDto> getDoctorsBySpecialty(Long specialtyId){
        List<Doctor> doctors = doctorRepository.findBySpecialty(specialtyId);
        return doctorMapper.toDtoList(doctors);
    }
}
