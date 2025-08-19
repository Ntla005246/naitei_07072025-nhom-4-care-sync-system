package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@Data
@NoArgsConstructor
public class JwtAuthResponseDto {
    private String accessToken;

    @Builder.Default
    private String tokenType = "Bearer";
}
