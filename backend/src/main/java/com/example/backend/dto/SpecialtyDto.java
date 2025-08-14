package com.example.backend.dto;

import com.example.backend.constant.MessageConstants;
import java.time.LocalDateTime;
import java.util.List;

public record SpecialtyDto(Integer id, String name, String description, LocalDateTime createdAt,
        LocalDateTime updatedAt, List<String> availableLocations, Long doctorCount) {
    // Compact constructor for validation
    public SpecialtyDto {
        if (id != null && id <= 0) {
            throw new IllegalArgumentException(MessageConstants.VALIDATION_SPECIALTY_ID_POSITIVE);
        }
        if (name != null && name.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    MessageConstants.VALIDATION_SPECIALTY_NAME_NOT_EMPTY);
        }
    }
}
