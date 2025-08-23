package com.example.backend.service;

import java.math.BigDecimal;

import com.example.backend.dto.InvoiceResponse;

public interface InvoiceService {

    InvoiceResponse createInvoiceForAppointment(Long appointmentId, BigDecimal totalAmount);

    InvoiceResponse getInvoiceByAppointmentId(Long appointmentId);

    String generateInvoiceCode();
} 
