package com.example.backend.mapper;

import com.example.backend.dto.ServiceResponse;
import com.example.backend.entity.Service;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ServiceMapper {

    @Mapping(target = "specialtyId", source = "specialty.id")
    @Mapping(target = "specialtyName", source = "specialty.name")
    ServiceResponse toResponse(Service entity);
}
