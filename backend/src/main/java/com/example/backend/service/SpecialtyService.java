package com.example.backend.service;

import com.example.backend.dto.PageResponse;
import com.example.backend.dto.SpecialtyDto;
import com.example.backend.dto.SpecialtySearchRequest;

public interface SpecialtyService {

    /**
     * Tìm kiếm chuyên khoa với filtering và pagination
     * 
     * @param request
     *            Thông tin tìm kiếm và phân trang
     * @return PageResponse chứa danh sách chuyên khoa
     */
    PageResponse<SpecialtyDto> searchSpecialties(SpecialtySearchRequest request);
}
