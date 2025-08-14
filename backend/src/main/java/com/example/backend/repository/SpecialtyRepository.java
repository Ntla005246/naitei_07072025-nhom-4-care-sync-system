package com.example.backend.repository;

import com.example.backend.entity.Specialty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpecialtyRepository extends JpaRepository<Specialty, Integer> {

    @Query("""
            SELECT DISTINCT s
            FROM Specialty s
            WHERE (:q IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :q, '%')))
            AND (:location IS NULL OR EXISTS (
                SELECT 1
                FROM s.doctors d
                WHERE LOWER(d.user.address) LIKE LOWER(CONCAT('%', :location, '%'))
            ))
            """)
    Page<Specialty> searchSpecialties(@Param("q") String q, @Param("location") String location,
            Pageable pageable);

    // Batch query để lấy doctor count cho nhiều specialties
    @Query("""
            SELECT s.id, COUNT(DISTINCT d.id)
            FROM Specialty s
            LEFT JOIN s.doctors d
            WHERE s.id IN :specialtyIds
            GROUP BY s.id
            """)
    List<Object[]> countDoctorsBySpecialtyIds(@Param("specialtyIds") List<Integer> specialtyIds);

    // Batch query để lấy locations cho nhiều specialties
    @Query("""
            SELECT s.id, u.address
            FROM Specialty s
            JOIN s.doctors d
            JOIN d.user u
            WHERE s.id IN :specialtyIds AND u.address IS NOT NULL
            """)
    List<Object[]> findAvailableLocationsBySpecialtyIds(
            @Param("specialtyIds") List<Integer> specialtyIds);

    @Query("""
            SELECT COUNT(DISTINCT d.id)
            FROM Specialty s
            LEFT JOIN s.doctors d
            WHERE s.id = :specialtyId
            """)
    Long countDoctorsBySpecialty(@Param("specialtyId") Integer specialtyId);

    @Query("""
            SELECT DISTINCT u.address
            FROM Specialty s
            JOIN s.doctors d
            JOIN d.user u
            WHERE s.id = :specialtyId AND u.address IS NOT NULL
            """)
    List<String> findAvailableLocationsBySpecialty(@Param("specialtyId") Integer specialtyId);
}
