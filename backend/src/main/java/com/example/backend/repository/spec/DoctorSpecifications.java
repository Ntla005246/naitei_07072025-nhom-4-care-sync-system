package com.example.backend.repository.spec;

import java.math.BigDecimal;

import org.springframework.data.jpa.domain.Specification;

import com.example.backend.entity.Doctor;

public final class DoctorSpecifications {
    private DoctorSpecifications() {
    }

    public static Specification<Doctor> userIsActive() {
        return (root, cq, cb) -> cb.isTrue(root.join("user").get("isActive"));
    }

    public static Specification<Doctor> searchTerm(String q) {
        if (q == null || q.isBlank())
            return null;
        return (root, cq, cb) -> {
            var user = root.join("user");
            var specialty = root.join("specialty");
            String like = "%" + q.toLowerCase() + "%";
            return cb.or(cb.like(cb.lower(user.get("fullName")), like),
                    cb.like(cb.lower(specialty.get("name")), like));
        };
    }

    public static Specification<Doctor> specialtyId(Long id) {
        if (id == null)
            return null;
        return (root, cq, cb) -> cb.equal(root.join("specialty").get("id"), id);
    }

    public static Specification<Doctor> experienceMin(Integer min) {
        if (min == null)
            return null;
        return (root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("experienceYears"), min);
    }

    public static Specification<Doctor> experienceMax(Integer max) {
        if (max == null)
            return null;
        return (root, cq, cb) -> cb.lessThanOrEqualTo(root.get("experienceYears"), max);
    }

    public static Specification<Doctor> feeMin(BigDecimal min) {
        if (min == null)
            return null;
        return (root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("consultationFee"), min);
    }

    public static Specification<Doctor> feeMax(BigDecimal max) {
        if (max == null)
            return null;
        return (root, cq, cb) -> cb.lessThanOrEqualTo(root.get("consultationFee"), max);
    }
}
