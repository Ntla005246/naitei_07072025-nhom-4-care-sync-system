package com.example.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.backend.constant.enums.PaymentStatus;
import com.example.backend.entity.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByTransactionCode(String transactionCode);

    List<Payment> findByInvoiceId(Long invoiceId);

    List<Payment> findByStatus(PaymentStatus status);

    @Query("SELECT p FROM Payment p WHERE p.invoice.id = :invoiceId AND p.status = :status")
    List<Payment> findByInvoiceIdAndStatus(@Param("invoiceId") Long invoiceId, @Param("status") PaymentStatus status);

    @Query("SELECT p FROM Payment p WHERE p.status = :status AND p.createdAt < :expiryTime")
    List<Payment> findExpiredPayments(@Param("status") PaymentStatus status, @Param("expiryTime") java.time.LocalDateTime expiryTime);

    boolean existsByTransactionCode(String transactionCode);
} 
