package com.example.backend.service;

import com.example.backend.dto.ServiceCreateRequest;
import com.example.backend.dto.ServiceResponse;
import com.example.backend.dto.ServiceUpdateRequest;

public interface ServiceAdminService {
    ServiceResponse create(ServiceCreateRequest req);
    ServiceResponse update(Long id, ServiceUpdateRequest req);
}
