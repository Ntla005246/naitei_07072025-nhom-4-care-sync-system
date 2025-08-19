package com.example.backend.dto;

import java.math.BigDecimal;

import org.springframework.data.domain.Sort;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

public record DoctorSearchRequest(
        // Tìm kiếm
        String searchTerm,

        // Lọc
        Long specialtyId,
        @Min(value = 0, message = "minExperience must be >= 0") Integer minExperience,
        @Min(value = 0, message = "maxExperience must be >= 0") Integer maxExperience,
        @DecimalMin(value = "0.0", inclusive = true, message = "minFee must be >= 0") BigDecimal minFee,
        @DecimalMin(value = "0.0", inclusive = true, message = "maxFee must be >= 0") BigDecimal maxFee,

        @Min(value = 0, message = "page must be >= 0") Integer page,
        @Min(value = 1, message = "size must be >= 1") @Max(value = 200, message = "size must be <= 200") Integer size,

        @Pattern(regexp = "(?i)id|experienceYears|consultationFee|title", message = "sortBy is invalid") String sortBy,
        @Pattern(regexp = "(?i)ASC|DESC", message = "sortDirection must be ASC or DESC") String sortDirection) {

    public DoctorSearchRequest {
        if (page == null)
            page = 0;
        if (size == null)
            size = 10;
        if (sortBy == null)
            sortBy = "id";
        if (sortDirection == null)
            sortDirection = "ASC";
    }

    @AssertTrue(message = "minExperience must be <= maxExperience")
    public boolean isExperienceRangeValid() {
        return minExperience == null || maxExperience == null || minExperience <= maxExperience;
    }

    @AssertTrue(message = "minFee must be <= maxFee")
    public boolean isFeeRangeValid() {
        if (minFee == null || maxFee == null)
            return true;
        return minFee.compareTo(maxFee) <= 0;
    }

    public Sort getSort() {
        Sort.Direction direction = Sort.Direction.fromString(sortDirection.toUpperCase());
        return Sort.by(direction, sortBy);
    }
}
