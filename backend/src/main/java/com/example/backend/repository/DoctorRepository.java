package com.example.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.backend.entity.Doctor;

@Repository
public interface DoctorRepository
        extends
            JpaRepository<Doctor, Long>,
            org.springframework.data.jpa.repository.JpaSpecificationExecutor<Doctor> {

    @Query("""
            SELECT d
            FROM Doctor d
            JOIN FETCH d.user u
            JOIN FETCH d.specialty s
            WHERE d.user.isActive = true
            """)
    List<Doctor> findAllActiveDoctors();

    @Query("""
            SELECT d
            FROM Doctor d
            JOIN FETCH d.user u
            JOIN FETCH d.specialty s
            WHERE d.specialty.id = :specialtyId
            """)
    List<Doctor> findBySpecialty(@Param("specialtyId") Long specialtyId);

}
