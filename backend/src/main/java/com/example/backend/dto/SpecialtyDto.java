package com.example.backend.dto;

import java.time.LocalDateTime;
import java.util.List;
public record SpecialtyDto(Integer id, String name, String description, LocalDateTime createdAt,
        LocalDateTime updatedAt, List<String> availableLocations, Long doctorCount) {
}
