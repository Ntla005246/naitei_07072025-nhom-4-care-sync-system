package com.example.backend.service;

import com.example.backend.constant.enums.PaymentMethod;
import com.example.backend.constant.enums.PaymentStatus;
import com.example.backend.dto.PaymentRequest;
import com.example.backend.dto.PaymentResponse;

public interface PaymentService {

    PaymentResponse createPayment(PaymentRequest request);

    PaymentStatus getPaymentStatus(String transactionCode);

    PaymentStatus getPaymentStatusById(Long paymentId);

    PaymentResponse processCallback(PaymentMethod method, String transactionCode, String signature, Object callbackData);

    PaymentResponse refundPayment(String transactionCode, String reason);

    PaymentResponse refundPaymentById(Long paymentId, String reason);

    PaymentResponse cancelPayment(String transactionCode);

    PaymentResponse cancelPaymentById(Long paymentId);
} 
