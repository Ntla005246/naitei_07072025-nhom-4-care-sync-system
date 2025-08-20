package com.example.backend.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.constant.ApiConstants;
import com.example.backend.constant.MockConstants;
import com.example.backend.constant.enums.MockPaymentStatus;
import com.example.backend.constant.enums.PaymentMethod;
import com.example.backend.dto.ApiResponse;
import com.example.backend.dto.PaymentResponse;
import com.example.backend.entity.Payment;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.PaymentRepository;
import com.example.backend.service.PaymentFactoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(ApiConstants.PAYMENTS_ENDPOINT + "/mock")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Mock Payment", description = "Mock payment gateway for testing")
public class MockPaymentController {

	private final PaymentFactoryService paymentFactoryService;
	private final MessageSource messageSource;
	private final PaymentRepository paymentRepository;

	@PostMapping("/momo/callback")
	@Operation(summary = "Mock MoMo callback", description = "Simulate MoMo payment callback for testing")
	public ApiResponse<PaymentResponse> mockMomoCallback(@RequestParam Long paymentId,
			@RequestParam(defaultValue = "SUCCESS") String status,
			@RequestParam(defaultValue = MockConstants.DEFAULT_AMOUNT) String amount,
			@RequestParam(defaultValue = MockConstants.DEFAULT_SIGNATURE) String signature) {

		log.info("Mock MoMo callback: paymentId={}, status={}, amount={}", paymentId, status, amount);

		Payment payment = findPaymentOrThrow(paymentId);
		Map<String, Object> callbackData = buildCallbackData(payment, status, amount, signature);

		PaymentResponse response = paymentFactoryService.processCallback(PaymentMethod.MOMO,
				payment.getTransactionCode(), signature, callbackData);

		String message = messageSource.getMessage("success.payment.callback.processed", null,
				LocaleContextHolder.getLocale());

		return ApiResponse.success(response, message);
	}

	@PostMapping("/vnpay/callback")
	@Operation(summary = "Mock VNPay callback", description = "Simulate VNPay payment callback for testing")
	public ApiResponse<PaymentResponse> mockVnpayCallback(@RequestParam Long paymentId,
			@RequestParam(defaultValue = "SUCCESS") String status,
			@RequestParam(defaultValue = MockConstants.DEFAULT_AMOUNT) String amount,
			@RequestParam(defaultValue = MockConstants.DEFAULT_SIGNATURE) String signature) {

		log.info("Mock VNPay callback: paymentId={}, status={}, amount={}", paymentId, status, amount);

		Payment payment = findPaymentOrThrow(paymentId);
		Map<String, Object> callbackData = buildCallbackData(payment, status, amount, signature);

		PaymentResponse response = paymentFactoryService.processCallback(PaymentMethod.VNPAY,
				payment.getTransactionCode(), signature, callbackData);

		String message = messageSource.getMessage("success.payment.callback.processed", null,
				LocaleContextHolder.getLocale());

		return ApiResponse.success(response, message);
	}

	@PostMapping("/simulate-payment-success")
	@Operation(summary = "Simulate successful payment", description = "Simulate a complete successful payment flow")
	public ApiResponse<Map<String, Object>> simulatePaymentSuccess(@RequestParam Long paymentId,
			@RequestParam PaymentMethod method) {

		log.info("Simulating successful payment: paymentId={}, method={}", paymentId, method);

		Payment payment = findPaymentOrThrow(paymentId);
		Map<String, Object> callbackData = buildCallbackData(payment, MockPaymentStatus.SUCCESS.getValue(), 
				payment.getAmount().toString(), MockConstants.DEFAULT_SIGNATURE);

		PaymentResponse paymentResponse = paymentFactoryService.processCallback(method, payment.getTransactionCode(),
				MockConstants.DEFAULT_SIGNATURE, callbackData);

		Map<String, Object> result = new HashMap<>();
		result.put("payment", paymentResponse);
		result.put("simulation", MockPaymentStatus.SUCCESS.getValue());
		result.put("message", "Payment simulation completed successfully");

		String message = messageSource.getMessage("success.payment.simulation.completed", null,
				LocaleContextHolder.getLocale());

		return ApiResponse.success(result, message);
	}

	@PostMapping("/simulate-payment-failure")
	@Operation(summary = "Simulate failed payment", description = "Simulate a failed payment scenario")
	public ApiResponse<Map<String, Object>> simulatePaymentFailure(@RequestParam Long paymentId,
			@RequestParam PaymentMethod method,
			@RequestParam(defaultValue = MockConstants.DEFAULT_FAILURE_REASON) String failureReason) {

		log.info("Simulating failed payment: paymentId={}, method={}, reason={}", paymentId, method, failureReason);

		Payment payment = findPaymentOrThrow(paymentId);
		Map<String, Object> callbackData = new HashMap<>();
		callbackData.put("transactionCode", payment.getTransactionCode());
		callbackData.put("status", MockPaymentStatus.FAILED.getValue());
		callbackData.put("errorCode", failureReason);
		callbackData.put("errorMessage", "Payment failed: " + failureReason);

		PaymentResponse paymentResponse = paymentFactoryService.processCallback(method, payment.getTransactionCode(),
				MockConstants.DEFAULT_SIGNATURE, callbackData);

		Map<String, Object> result = new HashMap<>();
		result.put("payment", paymentResponse);
		result.put("simulation", MockPaymentStatus.FAILED.getValue());
		result.put("failureReason", failureReason);

		String message = messageSource.getMessage("success.payment.simulation.failed", null,
				LocaleContextHolder.getLocale());

		return ApiResponse.success(result, message);
	}

	@GetMapping("/test-scenarios")
	@Operation(summary = "Get test scenarios", description = "Get list of available test scenarios")
	public ApiResponse<Map<String, Object>> getTestScenarios() {
		Map<String, Object> scenarios = new HashMap<>();

		scenarios.put("successful_payment",
				Map.of("description", "Simulate successful payment", "endpoint", "POST /mock-payments/simulate-payment-success",
						"parameters", Map.of("paymentId", "123", "method", "MOMO")));

		scenarios.put("failed_payment",
				Map.of("description", "Simulate failed payment", "endpoint", "POST /mock-payments/simulate-payment-failure",
						"parameters", Map.of("paymentId", "123", "method", "MOMO", "failureReason", MockConstants.DEFAULT_FAILURE_REASON)));

		scenarios.put("momo_callback",
				Map.of("description", "Simulate MoMo callback", "endpoint", "POST /mock-payments/momo/callback",
						"parameters", Map.of("paymentId", "123", "status", MockPaymentStatus.SUCCESS.getValue(), "amount", MockConstants.DEFAULT_AMOUNT)));

		scenarios.put("vnpay_callback",
				Map.of("description", "Simulate VNPay callback", "endpoint", "POST /mock-payments/vnpay/callback",
						"parameters", Map.of("paymentId", "456", "status", MockPaymentStatus.SUCCESS.getValue(), "amount", "750000")));

		return ApiResponse.success(scenarios, "Available test scenarios");
	}

	/**
	 * Tìm payment theo ID, nếu không tìm thấy thì throw ResourceNotFoundException
	 * 
	 * @param paymentId ID của payment cần tìm
	 * @return Payment entity
	 * @throws ResourceNotFoundException nếu không tìm thấy payment
	 */
	private Payment findPaymentOrThrow(Long paymentId) {
		return paymentRepository.findById(paymentId)
				.orElseThrow(() -> new ResourceNotFoundException("error.payment.not.found", paymentId));
	}

	/**
	 * Tạo callback data cho payment gateway
	 * 
	 * @param payment Payment entity
	 * @param status Trạng thái payment (SUCCESS/FAILED)
	 * @param amount Số tiền
	 * @param signature Chữ ký
	 * @return Map chứa callback data
	 */
	private Map<String, Object> buildCallbackData(Payment payment, String status, String amount, String signature) {
		Map<String, Object> callbackData = new HashMap<>();
		callbackData.put("transactionCode", payment.getTransactionCode());
		callbackData.put("status", status);
		callbackData.put("amount", amount);
		callbackData.put("signature", signature);
		return callbackData;
	}
} 
