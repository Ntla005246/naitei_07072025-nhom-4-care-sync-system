package com.example.backend.dto;

import com.example.backend.constant.PagingConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

@Getter
@Setter
@ToString
@Schema(description = "Request parameters for searching specialties")
public class SpecialtySearchRequest {

    @Schema(description = "Search query for specialty name", example = "cardio")
    private String q; // Query search theo tên chuyên khoa

    @Schema(description = "Filter by doctor location", example = "Hanoi")
    private String location; // Lọc theo địa điểm của bác sĩ

    @Min(value = 0, message = "{validation.page.number.min}")
    @Schema(description = "Page number (0-based)", example = "0")
    private Integer page; // Trang hiện tại

    @Min(value = 1, message = "{validation.page.size.min}")
    @Schema(description = "Number of items per page", example = "10")
    private Integer size; // Số lượng item per page

    @Pattern(regexp = "^(name|description|createdAt)$", message = "{validation.sort.field.invalid}")
    @Schema(description = "Field to sort by", example = "name", allowableValues = {"name",
            "description", "createdAt"})
    private String sortBy; // Sắp xếp theo trường

    @Pattern(regexp = "^(asc|desc)$", message = "{validation.sort.direction.invalid}")
    @Schema(description = "Sort direction", example = "asc", allowableValues = {"asc", "desc"})
    private String sortDirection; // Hướng sắp xếp (asc/desc)

    // Default constructor với giá trị mặc định
    public SpecialtySearchRequest() {
        this.page = Integer.parseInt(PagingConstants.DEFAULT_PAGE_NUMBER);
        this.size = Integer.parseInt(PagingConstants.DEFAULT_PAGE_SIZE);
        this.sortBy = PagingConstants.DEFAULT_SORT_BY;
        this.sortDirection = PagingConstants.DEFAULT_SORT_DIRECTION;
    }
}
