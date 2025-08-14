package com.example.backend.dto;

import com.example.backend.constant.MessageConstants;
import com.example.backend.constant.PagingConstants;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

@Getter
@Setter
@ToString
public class SpecialtySearchRequest {
    private String q; // Query search theo tên chuyên khoa
    private String location; // Lọc theo địa điểm của bác sĩ

    @Min(value = 0, message = MessageConstants.VALIDATION_PAGE_NUMBER_MIN)
    private Integer page; // Trang hiện tại

    @Min(value = 1, message = MessageConstants.VALIDATION_PAGE_SIZE_MIN)
    private Integer size; // Số lượng item per page

    @Pattern(regexp = "^(name|description|createdAt)$", message = MessageConstants.VALIDATION_SORT_FIELD_INVALID)
    private String sortBy; // Sắp xếp theo trường

    @Pattern(regexp = "^(asc|desc)$", message = MessageConstants.VALIDATION_SORT_DIRECTION_INVALID)
    private String sortDirection; // Hướng sắp xếp (asc/desc)

    // Default constructor với giá trị mặc định
    public SpecialtySearchRequest() {
        this.page = Integer.parseInt(PagingConstants.DEFAULT_PAGE_NUMBER);
        this.size = Integer.parseInt(PagingConstants.DEFAULT_PAGE_SIZE);
        this.sortBy = PagingConstants.DEFAULT_SORT_BY;
        this.sortDirection = PagingConstants.DEFAULT_SORT_DIRECTION;
    }
}
