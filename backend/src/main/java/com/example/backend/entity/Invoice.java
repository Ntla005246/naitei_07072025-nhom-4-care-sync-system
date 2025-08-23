package com.example.backend.entity;

import com.example.backend.constant.enums.InvoiceStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import jakarta.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "invoices")
public class Invoice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", nullable = false, unique = true)
    private Appointment appointment;

    @NotNull(message = "Invoice code is required")
    @Column(name = "invoice_code", nullable = false, unique = true)
    private String invoiceCode;

    @NotNull(message = "Invoice amount is required")
    @DecimalMin(value = "0.01", message = "Invoice amount must be greater than 0")
    @DecimalMax(value = "100000000.00", message = "Invoice amount cannot exceed 100,000,000 VND")
    @Digits(integer = 9, fraction = 2, message = "Invoice amount must have at most 9 digits and 2 decimal places")
    @Column(name = "final_amount", nullable = false)
    private BigDecimal finalAmount;

    @NotNull(message = "Invoice status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private InvoiceStatus status;

    @NotNull(message = "Issued date is required")
    @Column(name = "issued_date", nullable = false)
    private LocalDateTime issuedDate;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Payment> payments;
}
