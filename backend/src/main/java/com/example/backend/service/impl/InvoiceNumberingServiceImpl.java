package com.example.backend.service.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class InvoiceNumberingServiceImpl {

    private static final String INVOICE_PREFIX = "INV";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter
            .ofPattern("yyyyMMddHHmmss");

    // Sequence counter cho mỗi ngày, reset về 1 mỗi ngày mới
    private final AtomicLong dailySequence = new AtomicLong(1);
    private volatile String currentDate = LocalDate.now().format(DATE_FORMATTER);

    /**
     * Generate invoice code với format: INV-YYYYMMDD-XXXXXX Trong đó XXXXXX là
     * sequence number 6 chữ số, reset mỗi ngày
     */
    public String generateInvoiceCode() {
        String today = LocalDate.now().format(DATE_FORMATTER);

        // Reset sequence nếu sang ngày mới
        if (!today.equals(currentDate)) {
            synchronized (this) {
                if (!today.equals(currentDate)) {
                    dailySequence.set(1);
                    currentDate = today;
                    log.info("Reset invoice sequence for new day: {}", today);
                }
            }
        }

        long sequence = dailySequence.getAndIncrement();
        String invoiceCode = String.format("%s-%s-%06d", INVOICE_PREFIX, today, sequence);

        log.debug("Generated invoice code: {}", invoiceCode);
        return invoiceCode;
    }
}
