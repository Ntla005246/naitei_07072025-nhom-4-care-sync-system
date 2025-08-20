package com.example.backend.dto;

import com.example.backend.constant.PagingConstants;
import com.example.backend.constant.enums.AppointmentStatus;
import jakarta.validation.constraints.Min;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public class AppointmentListRequest {

    @Min(0)
    private int page = Integer.parseInt(PagingConstants.DEFAULT_PAGE_NUMBER);

    @Min(1)
    private int size = Integer.parseInt(PagingConstants.DEFAULT_PAGE_SIZE);

    private AppointmentStatus status; // optional

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startFrom; // optional

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startTo; // optional

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public LocalDateTime getStartFrom() {
        return startFrom;
    }

    public void setStartFrom(LocalDateTime startFrom) {
        this.startFrom = startFrom;
    }

    public LocalDateTime getStartTo() {
        return startTo;
    }

    public void setStartTo(LocalDateTime startTo) {
        this.startTo = startTo;
    }
}
