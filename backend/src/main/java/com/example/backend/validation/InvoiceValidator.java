package com.example.backend.validation;

import java.math.BigDecimal;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import com.example.backend.entity.Invoice;
import com.example.backend.exception.BusinessException;
import com.example.backend.repository.InvoiceRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class InvoiceValidator {

    private final InvoiceRepository invoiceRepository;

    public void validateInvoiceAmount(BigDecimal amount) {
        if (amount == null) {
            throw new BusinessException("error.invoice.amount.required");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("error.invoice.amount.must.be.positive");
        }

        if (amount.compareTo(new BigDecimal("10000")) < 0) {
            throw new BusinessException("error.invoice.amount.too.small");
        }

        if (amount.compareTo(new BigDecimal("100000000")) > 0) {
            throw new BusinessException("error.invoice.amount.too.large");
        }

        if (amount.scale() > 2) {
            throw new BusinessException("error.invoice.amount.decimal.places");
        }
    }

    public void validateInvoiceCreation(Long appointmentId) {
        if (appointmentId == null || appointmentId <= 0) {
            throw new BusinessException("error.invoice.appointment.id.invalid");
        }

        if (invoiceRepository.findByAppointmentId(appointmentId).isPresent()) {
            throw new BusinessException("error.invoice.already.exists", appointmentId);
        }
    }

    public void validateInvoiceCode(String invoiceCode) {
        if (invoiceCode == null || invoiceCode.trim().isEmpty()) {
            throw new BusinessException("error.invoice.code.required");
        }

        if (!invoiceCode.matches("^INV-\\d{8}-\\d{6}$")) {
            throw new BusinessException("error.invoice.code.format.invalid");
        }

        if (invoiceCode.length() > 100) {
            throw new BusinessException("error.invoice.code.too.long");
        }

        if (invoiceRepository.existsByInvoiceCode(invoiceCode)) {
            throw new BusinessException("error.invoice.code.already.exists");
        }
    }

    private <T> T validateEntityExists(Long id,
            Function<Long, java.util.Optional<T>> findByIdFunction, String errorMessage) {
        return findByIdFunction.apply(id).orElseThrow(() -> new BusinessException(errorMessage));
    }

    public Invoice validateInvoiceExists(Long invoiceId) {
        return validateEntityExists(invoiceId, invoiceRepository::findById,
                "error.invoice.not.found");
    }
}
