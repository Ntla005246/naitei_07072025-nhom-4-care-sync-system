package com.example.backend.service.impl;

import com.example.backend.dto.InvoiceResponse;
import com.example.backend.entity.Appointment;
import com.example.backend.entity.AppointmentSlot;
import com.example.backend.entity.Doctor;
import com.example.backend.entity.Invoice;
import com.example.backend.entity.Patient;
import com.example.backend.entity.User;
import com.example.backend.entity.AppointmentService;
import com.example.backend.constant.enums.InvoiceStatus;
import com.example.backend.exception.BusinessException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.exception.ResourceAlreadyExistsException;
import com.example.backend.repository.AppointmentRepository;
import com.example.backend.repository.AppointmentServiceRepository;
import com.example.backend.repository.InvoiceRepository;
import com.example.backend.service.InvoiceService;
import com.example.backend.validation.InvoiceValidator;
import com.example.backend.service.impl.InvoiceNumberingServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final AppointmentRepository appointmentRepository;
    private final AppointmentServiceRepository appointmentServiceRepository;
    private final InvoiceValidator invoiceValidator;
    private final InvoiceNumberingServiceImpl invoiceNumberingService;

    @Override
    @Transactional
    public InvoiceResponse createInvoiceForAppointment(Long appointmentId, BigDecimal totalAmount) {
        log.info("Creating invoice automatically for appointment: {} with amount: {}", appointmentId, totalAmount);
        return createInvoiceInternal(appointmentId, totalAmount);
    }

    @Override
    public InvoiceResponse getInvoiceByAppointmentId(Long appointmentId) {
        log.info("Getting invoice by appointment ID: {}", appointmentId);

        Invoice invoice = invoiceRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("error.invoice.not.found", appointmentId));

        return mapToInvoiceResponse(invoice);
    }



    @Override
    public String generateInvoiceCode() {
        return invoiceNumberingService.generateInvoiceCode();
    }

    private InvoiceResponse createInvoiceInternal(Long appointmentId, BigDecimal totalAmount) {
        // Validate appointment exists
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("error.appointment.not.found", appointmentId));

        // Validate invoice creation
        invoiceValidator.validateInvoiceCreation(appointmentId);
        
        // Validate invoice amount
        invoiceValidator.validateInvoiceAmount(totalAmount);

        String invoiceCode = invoiceNumberingService.generateInvoiceCode();
        invoiceValidator.validateInvoiceCode(invoiceCode);
        
        Invoice invoice = new Invoice();
        invoice.setAppointment(appointment);
        invoice.setInvoiceCode(invoiceCode);
        invoice.setFinalAmount(totalAmount);
        invoice.setStatus(InvoiceStatus.PENDING);
        invoice.setIssuedDate(LocalDateTime.now());

        Invoice savedInvoice = invoiceRepository.save(invoice);
        log.info("Created invoice: {}", savedInvoice.getInvoiceCode());

        return mapToInvoiceResponse(savedInvoice);
    }



    private InvoiceResponse mapToInvoiceResponse(Invoice invoice) {
        Appointment appointment = invoice.getAppointment();
        if (appointment == null) {
            throw new BusinessException("error.invoice.appointment.missing");
        }
        
        Patient patient = appointment.getPatient();
        if (patient == null) {
            throw new BusinessException("error.invoice.patient.missing");
        }
        
        User patientUser = patient.getUser();
        if (patientUser == null) {
            throw new BusinessException("error.invoice.patient.user.missing");
        }
        
        AppointmentSlot slot = appointment.getAppointmentSlot();
        Doctor doctor = slot != null ? slot.getDoctor() : null;
        User doctorUser = doctor != null ? doctor.getUser() : null;
        
        List<AppointmentService> appointmentServices = appointmentServiceRepository.findByAppointmentId(appointment.getId());
        List<InvoiceResponse.ServiceItem> services = appointmentServices.stream()
                .map(as -> new InvoiceResponse.ServiceItem(
                        as.getService().getId(),
                        as.getService().getName(),
                        as.getPriceAtBooking()))
                .collect(Collectors.toList());

        return new InvoiceResponse(
                invoice.getId(),
                invoice.getInvoiceCode(),
                appointment.getId(),
                invoice.getFinalAmount(),
                invoice.getStatus(),
                invoice.getIssuedDate(),
                new InvoiceResponse.PatientInfo(
                        patient.getId(),
                        patientUser.getFullName(),
                        patientUser.getEmail(),
                        patientUser.getPhoneNumber()
                ),
                doctor != null ? new InvoiceResponse.DoctorInfo(
                        doctor.getId(),
                        doctorUser != null ? doctorUser.getFullName() : null,
                        doctor.getSpecialty() != null ? doctor.getSpecialty().getName() : null,
                        doctor.getConsultationFee()
                ) : null,
                services,
                invoice.getCreatedAt(),
                invoice.getUpdatedAt()
        );
    }
} 
