package com.example.backend.constant.enums;

public enum PaymentStatus {
    PENDING,    // Chờ thanh toán
    PROCESSING, // Đang xử lý
    SUCCESSFUL, // Thanh toán thành công
    FAILED,     // Thanh toán thất bại
    CANCELLED,  // Đã hủy
    REFUNDED,   // Đã hoàn tiền
    EXPIRED     // Hết hạn
}
