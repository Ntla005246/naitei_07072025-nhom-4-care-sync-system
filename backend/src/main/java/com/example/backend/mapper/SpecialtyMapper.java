package com.example.backend.mapper;

import com.example.backend.dto.SpecialtyDto;
import com.example.backend.entity.Specialty;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SpecialtyMapper {

    @Mapping(target = "availableLocations", ignore = true)
    @Mapping(target = "doctorCount", ignore = true)
    SpecialtyDto toDto(Specialty specialty);

    List<SpecialtyDto> toDtoList(List<Specialty> specialties);

    @Named("toDtoWithDetails")
    @Mapping(target = "availableLocations", ignore = true)
    @Mapping(target = "doctorCount", ignore = true)
    SpecialtyDto toDtoWithDetails(Specialty specialty);
}
