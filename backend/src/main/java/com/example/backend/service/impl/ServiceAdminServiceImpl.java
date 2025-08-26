package com.example.backend.service.impl;

import com.example.backend.dto.ServiceCreateRequest;
import com.example.backend.dto.ServiceResponse;
import com.example.backend.dto.ServiceUpdateRequest;
import com.example.backend.entity.Service;
import com.example.backend.exception.BusinessException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.mapper.ServiceMapper;
import com.example.backend.repository.ServiceRepository;
import com.example.backend.repository.SpecialtyRepository;
import com.example.backend.service.ServiceAdminService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import java.util.Objects;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceAdminServiceImpl implements ServiceAdminService {

    private final ServiceRepository serviceRepository;
    private final SpecialtyRepository specialtyRepository;
    private final ServiceMapper serviceMapper;

    @Override
    @Transactional
    public ServiceResponse create(ServiceCreateRequest req) {
        var sp = specialtyRepository.findById(req.specialtyId())
                .orElseThrow(() -> new ResourceNotFoundException("error.specialty.not.found"));

        if (serviceRepository.existsBySpecialtyIdAndNameIgnoreCase(req.specialtyId(),
                req.name().trim())) {
            throw new BusinessException("error.service.duplicate");
        }

        var e = new Service();
        e.setSpecialty(sp);
        e.setName(req.name().trim());
        e.setDescription(req.description());
        e.setDurationMinutes(req.durationMinutes());
        e.setPrice(req.price());

        return serviceMapper.toResponse(serviceRepository.save(e));
    }

    @Override
    @Transactional
    public ServiceResponse update(Long id, ServiceUpdateRequest req) {
        var e = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("error.service.not.found"));

        if (req.specialtyId() != null
                && !Objects.equals(req.specialtyId(), e.getSpecialty().getId())) {
            var sp = specialtyRepository.findById(req.specialtyId())
                    .orElseThrow(() -> new ResourceNotFoundException("error.specialty.not.found"));
            e.setSpecialty(sp);
        }

        if (req.name() != null && !req.name().trim().isEmpty()) {
            var newName = req.name().trim();
            var spId = e.getSpecialty().getId(); // Integer
            if (serviceRepository.existsBySpecialtyIdAndNameIgnoreCaseAndIdNot(spId, newName,
                    e.getId())) {
                throw new BusinessException("error.service.duplicate");
            }
            e.setName(newName);
        }

        if (req.description() != null)
            e.setDescription(req.description());
        if (req.durationMinutes() != null)
            e.setDurationMinutes(req.durationMinutes());
        if (req.price() != null)
            e.setPrice(req.price());

        return serviceMapper.toResponse(serviceRepository.save(e));
    }
}
