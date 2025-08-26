package com.example.backend.controller.admin;

import com.example.backend.constant.ApiConstants;
import com.example.backend.dto.ApiResponse;
import com.example.backend.dto.ServiceCreateRequest;
import com.example.backend.dto.ServiceResponse;
import com.example.backend.dto.ServiceUpdateRequest;
import com.example.backend.service.ServiceAdminService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiConstants.ADMIN_ENDPOINT + "/services")
@RequiredArgsConstructor
public class AdminServiceController {

    private final ServiceAdminService service;
    private final MessageSource messageSource;

    @Operation(summary = "Create a new medical service")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ServiceResponse> create(@Valid @RequestBody ServiceCreateRequest req) {
        ServiceResponse resp = service.create(req);
        String msg = messageSource.getMessage("success.service.created", null,
                LocaleContextHolder.getLocale());
        return ApiResponse.success(resp, msg);
    }

    @Operation(summary = "Update an existing medical service")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ServiceResponse> update(@PathVariable Long id,
            @Valid @RequestBody ServiceUpdateRequest req) {
        ServiceResponse resp = service.update(id, req);
        String msg = messageSource.getMessage("success.service.updated", null,
                LocaleContextHolder.getLocale());
        return ApiResponse.success(resp, msg);
    }
}
