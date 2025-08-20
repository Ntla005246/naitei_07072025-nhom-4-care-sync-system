package com.example.backend.service.impl;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.constant.enums.PaymentMethod;
import com.example.backend.constant.enums.PaymentStatus;
import com.example.backend.dto.PaymentRequest;
import com.example.backend.dto.PaymentResponse;
import com.example.backend.entity.Invoice;
import com.example.backend.entity.Payment;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.PaymentRepository;
import com.example.backend.service.PaymentService;
import com.example.backend.service.PaymentValidationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MomoPaymentService implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentValidationService validationService;

    @Value("${payment.momo.partner-code}")
    private String partnerCode;

    @Value("${payment.momo.access-key}")
    private String accessKey;

    @Value("${payment.momo.secret-key}")
    private String secretKey;

    @Value("${payment.momo.endpoint}")
    private String endpoint;

    @Value("${payment.momo.return-url}")
    private String returnUrl;

    @Value("${payment.momo.notify-url}")
    private String notifyUrl;

    @Override
    public PaymentResponse createPayment(PaymentRequest request) {
        log.info("Creating MoMo payment for invoice: {}", request.invoiceId());

        validationService.validateInvoiceForPayment(request.invoiceId());
        validationService.validatePendingPaymentsLimit(request.invoiceId());
       
        Invoice invoice = validationService.validateInvoiceExists(request.invoiceId());

        String transactionCode = generateTransactionCode();
        validationService.validateTransactionCodeUnique(transactionCode);

        Payment payment = new Payment();
        payment.setInvoice(invoice);
        payment.setAmount(invoice.getFinalAmount());
        payment.setPaymentMethod(PaymentMethod.MOMO);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setTransactionCode(transactionCode);

        payment = paymentRepository.save(payment);
        String paymentUrl = createMomoPaymentUrl(payment, request);

        return buildPaymentResponse(payment, paymentUrl);
    }

    @Override
    public PaymentStatus getPaymentStatus(String transactionCode) {
        Payment payment = paymentRepository.findByTransactionCode(transactionCode)
                .orElseThrow(() -> new ResourceNotFoundException("error.payment.not.found", transactionCode));
        return payment.getStatus();
    }

    @Override
    public PaymentStatus getPaymentStatusById(Long paymentId) {
        Payment payment = validationService.validatePaymentExists(paymentId);
        return payment.getStatus();
    }

    @Override
    public PaymentResponse processCallback(PaymentMethod method, String transactionCode, String signature, Object callbackData) {
        log.info("Processing MoMo callback for transaction: {}", transactionCode);

        Payment payment = paymentRepository.findByTransactionCode(transactionCode)
                .orElseThrow(() -> new ResourceNotFoundException("error.payment.not.found", transactionCode));
        
        // Check if payment is already processed to avoid overwriting status
        if (payment.getStatus() == PaymentStatus.SUCCESSFUL || payment.getStatus() == PaymentStatus.CANCELLED) {
            log.warn("Payment {} is already processed with status: {}", transactionCode, payment.getStatus());
            return buildPaymentResponse(payment, null);
        }

        payment.setStatus(PaymentStatus.SUCCESSFUL);
        payment = paymentRepository.save(payment);

        return buildPaymentResponse(payment, null);
    }

    @Override
    public PaymentResponse refundPayment(String transactionCode, String reason) {
        log.info("Processing refund for transaction: {}", transactionCode);

        Payment payment = paymentRepository.findByTransactionCode(transactionCode)
                .orElseThrow(() -> new ResourceNotFoundException("error.payment.not.found", transactionCode));

        validationService.validatePaymentForRefund(payment.getId());

        payment.setStatus(PaymentStatus.REFUNDED);
        payment = paymentRepository.save(payment);

        return buildPaymentResponse(payment, null);
    }

    @Override
    public PaymentResponse cancelPayment(String transactionCode) {
        log.info("Cancelling payment for transaction: {}", transactionCode);

        Payment payment = paymentRepository.findByTransactionCode(transactionCode)
                .orElseThrow(() -> new ResourceNotFoundException("error.payment.not.found", transactionCode));

        validationService.validatePaymentForCancellation(payment.getId());

        payment.setStatus(PaymentStatus.CANCELLED);
        payment = paymentRepository.save(payment);

        return buildPaymentResponse(payment, null);
    }

    @Override
    public PaymentResponse refundPaymentById(Long paymentId, String reason) {
        Payment payment = validationService.validatePaymentExists(paymentId);
        return refundPayment(payment.getTransactionCode(), reason);
    }

    @Override
    public PaymentResponse cancelPaymentById(Long paymentId) {
        Payment payment = validationService.validatePaymentExists(paymentId);
        return cancelPayment(payment.getTransactionCode());
    }

    private String generateTransactionCode() {
        return "MOMO_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
    }

    private String createMomoPaymentUrl(Payment payment, PaymentRequest request) {
        return "https://test-payment.momo.vn/v2/gateway/api/create?transactionCode=" + payment.getTransactionCode();
    }

    /**
     * Build PaymentResponse from Payment entity
     * 
     * @param payment The payment entity
     * @param paymentUrl The payment URL (can be null for non-create operations)
     * @return PaymentResponse
     */
    private PaymentResponse buildPaymentResponse(Payment payment, String paymentUrl) {
        return new PaymentResponse(
                payment.getId(),
                payment.getInvoice().getId(),
                payment.getAmount(),
                payment.getPaymentMethod().toString(),
                payment.getStatus(),
                payment.getTransactionCode(),
                paymentUrl,
                payment.getCreatedAt()
        );
    }
} 
