package com.example.backend.repository;

import com.example.backend.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepository extends JpaRepository<Service, Long> {
    boolean existsBySpecialtyIdAndNameIgnoreCase(Integer specialtyId, String name);
    boolean existsBySpecialtyIdAndNameIgnoreCaseAndIdNot(Integer specialtyId, String name, Long id);
}
