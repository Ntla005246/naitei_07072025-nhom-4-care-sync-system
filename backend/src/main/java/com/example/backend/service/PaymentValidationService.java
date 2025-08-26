package com.example.backend.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

import org.springframework.stereotype.Service;

import com.example.backend.constant.enums.PaymentStatus;
import com.example.backend.constant.enums.InvoiceStatus;
import com.example.backend.entity.Invoice;
import com.example.backend.entity.Payment;
import com.example.backend.exception.BusinessException;
import com.example.backend.repository.PaymentRepository;
import com.example.backend.repository.InvoiceRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentValidationService {

    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;

    public void validateInvoiceForPayment(Long invoiceId) {
        if (invoiceId == null || invoiceId <= 0) {
            throw new BusinessException("error.invoice.invalid.id");
        }

        Invoice invoice = validateEntityExists(invoiceId, invoiceRepository::findById,
                "error.invoice.not.found");

        if (invoice.getStatus() != InvoiceStatus.PENDING) {
            throw new BusinessException("error.invoice.not.pending");
        }

        if (invoice.getFinalAmount() == null
                || invoice.getFinalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("error.invoice.invalid.amount");
        }

        // Check if invoice is expired (more than 30 days old)
        if (invoice.getIssuedDate().plusDays(30).isBefore(LocalDateTime.now())) {
            throw new BusinessException("error.invoice.expired");
        }
    }

    public void validatePaymentForRefund(Long paymentId) {
        Payment payment = validateEntityExists(paymentId, paymentRepository::findById,
                "error.payment.not.found");

        if (payment.getStatus() != PaymentStatus.SUCCESSFUL) {
            throw new BusinessException("error.payment.cannot.refund");
        }

        // Check if payment is too old for refund (more than 30 days)
        if (payment.getCreatedAt().plusDays(30).isBefore(LocalDateTime.now())) {
            throw new BusinessException("error.payment.refund.expired");
        }
    }

    public void validatePaymentForCancellation(Long paymentId) {
        Payment payment = validateEntityExists(paymentId, paymentRepository::findById,
                "error.payment.not.found");

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new BusinessException("error.payment.cannot.cancel");
        }

        // Check if payment is too old for cancellation (more than 24 hours)
        if (payment.getCreatedAt().plusHours(24).isBefore(LocalDateTime.now())) {
            throw new BusinessException("error.payment.cancel.expired");
        }
    }

    public void validateUserCanPayInvoice(Long invoiceId, Long userId) {
        Invoice invoice = validateEntityExists(invoiceId, invoiceRepository::findById,
                "error.invoice.not.found");

        // Validate that the invoice belongs to the user
        if (!invoice.getAppointment().getPatient().getId().equals(userId)) {
            throw new BusinessException("error.invoice.not.owned.by.user");
        }
    }

    public void validatePaymentAmount(Long invoiceId, BigDecimal paymentAmount) {
        if (paymentAmount == null || paymentAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("error.payment.amount.required");
        }

        // Check amount range (1,000 VND to 100,000,000 VND)
        if (paymentAmount.compareTo(new BigDecimal("1000")) < 0) {
            throw new BusinessException("error.payment.amount.too.small");
        }
        if (paymentAmount.compareTo(new BigDecimal("100000000")) > 0) {
            throw new BusinessException("error.payment.amount.too.large");
        }

        Invoice invoice = validateEntityExists(invoiceId, invoiceRepository::findById,
                "error.invoice.not.found");

        if (!invoice.getFinalAmount().equals(paymentAmount)) {
            throw new BusinessException("error.payment.amount.mismatch");
        }
    }

    public void validatePendingPaymentsLimit(Long invoiceId) {
        List<Payment> pendingPayments = paymentRepository.findByInvoiceIdAndStatus(invoiceId,
                PaymentStatus.PENDING);

        if (pendingPayments.size() >= 3) {
            throw new BusinessException("error.payment.too.many.pending");
        }
    }

    public void validateTransactionCodeUnique(String transactionCode) {
        if (transactionCode == null || transactionCode.trim().isEmpty()) {
            throw new BusinessException("error.payment.transaction.code.required");
        }

        if (paymentRepository.existsByTransactionCode(transactionCode)) {
            throw new BusinessException("error.payment.transaction.code.exists");
        }
    }

    /**
     * Generic method to validate entity existence
     * 
     * @param id
     *            The entity ID to validate
     * @param findByIdFunction
     *            Function to find entity by ID
     * @param errorMessage
     *            Error message if entity not found
     * @return The found entity
     * @throws BusinessException
     *             if entity not found
     */
    private <T> T validateEntityExists(Long id,
            Function<Long, java.util.Optional<T>> findByIdFunction, String errorMessage) {
        return findByIdFunction.apply(id).orElseThrow(() -> new BusinessException(errorMessage));
    }

    /**
     * Validate that invoice exists and return it
     * 
     * @param invoiceId
     *            The invoice ID to validate
     * @return The found invoice
     * @throws BusinessException
     *             if invoice not found
     */
    public Invoice validateInvoiceExists(Long invoiceId) {
        return validateEntityExists(invoiceId, invoiceRepository::findById,
                "error.invoice.not.found");
    }

    /**
     * Validate that payment exists and return it
     * 
     * @param paymentId
     *            The payment ID to validate
     * @return The found payment
     * @throws BusinessException
     *             if payment not found
     */
    public Payment validatePaymentExists(Long paymentId) {
        return validateEntityExists(paymentId, paymentRepository::findById,
                "error.payment.not.found");
    }
}
