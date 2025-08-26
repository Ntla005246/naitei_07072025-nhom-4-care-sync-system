package com.example.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.entity.Invoice;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
}
