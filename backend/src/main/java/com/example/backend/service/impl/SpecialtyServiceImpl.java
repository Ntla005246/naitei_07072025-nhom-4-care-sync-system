package com.example.backend.service.impl;

import com.example.backend.dto.PageResponse;
import com.example.backend.dto.SpecialtyDto;
import com.example.backend.dto.SpecialtySearchRequest;
import com.example.backend.entity.Specialty;
import com.example.backend.mapper.SpecialtyMapper;
import com.example.backend.repository.SpecialtyRepository;
import com.example.backend.service.SpecialtyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SpecialtyServiceImpl implements SpecialtyService {

    private final SpecialtyRepository specialtyRepository;
    private final SpecialtyMapper specialtyMapper;

    @Override
    public PageResponse<SpecialtyDto> searchSpecialties(SpecialtySearchRequest request) {
        log.info("Searching specialties with request: {}", request);

        // Tạo Pageable với sorting
        Sort sort = Sort.by(Sort.Direction.fromString(request.getSortDirection()),
                request.getSortBy());

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        // Thực hiện tìm kiếm
        Page<Specialty> specialtyPage = specialtyRepository.searchSpecialties(request.getQ(),
                request.getLocation(), pageable);

        // Batch enrich DTOs để tránh N+1 problem
        List<SpecialtyDto> specialtyDtos = batchEnrichSpecialtyDtos(specialtyPage.getContent());

        // Tạo Page<SpecialtyDto> để sử dụng PageResponse.of()
        Page<SpecialtyDto> specialtyDtoPage = new PageImpl<>(specialtyDtos, pageable,
                specialtyPage.getTotalElements());

        // Sử dụng utility method có sẵn
        PageResponse<SpecialtyDto> response = PageResponse.of(specialtyDtoPage);

        log.info("Found {} specialties", specialtyPage.getTotalElements());
        return response;
    }

    /**
     * Batch enrich specialty DTOs để tránh N+1 query problem
     * 
     * @param specialties
     *            Danh sách specialties cần enrich
     * @return Danh sách DTOs với thông tin đầy đủ
     */
    private List<SpecialtyDto> batchEnrichSpecialtyDtos(List<Specialty> specialties) {
        if (specialties.isEmpty()) {
            return List.of();
        }

        // Lấy danh sách specialty IDs
        List<Integer> specialtyIds = specialties.stream().map(Specialty::getId)
                .collect(Collectors.toList());

        // Batch query để lấy doctor counts
        Map<Integer, Long> doctorCountMap = specialtyRepository
                .countDoctorsBySpecialtyIds(specialtyIds).stream()
                .collect(Collectors.toMap(row -> (Integer) row[0], row -> (Long) row[1]));

        // Batch query để lấy available locations
        Map<Integer, List<String>> locationMap = specialtyRepository
                .findAvailableLocationsBySpecialtyIds(specialtyIds).stream()
                .collect(Collectors.groupingBy(row -> (Integer) row[0],
                        Collectors.mapping(row -> (String) row[1], Collectors.toList())));

        // Enrich DTOs - với Record, chúng ta tạo mới thay vì set
        return specialties.stream().map(specialty -> {
            SpecialtyDto baseDto = specialtyMapper.toDto(specialty);
            return new SpecialtyDto(baseDto.id(), baseDto.name(), baseDto.description(),
                    baseDto.createdAt(), baseDto.updatedAt(),
                    locationMap.getOrDefault(specialty.getId(), List.of()),
                    doctorCountMap.getOrDefault(specialty.getId(), 0L));
        }).collect(Collectors.toList());
    }
}
