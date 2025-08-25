package com.example.backend.constant;

public final class EmailConstants {
    private EmailConstants() {
    }

    public static final String WEBSITE_BASE_URL = "http://localhost:8080";

    public static String appointmentDetailUrl(Long appointmentId) {
        return WEBSITE_BASE_URL + "/appointments/" + appointmentId;
    }

    public static String doctorAppointmentDetailUrl(Long appointmentId) {
        return WEBSITE_BASE_URL + "/doctor/appointments/" + appointmentId;
    }

    public static String doctorConfirmAppointmentUrl(Long appointmentId) {
        return WEBSITE_BASE_URL + "/doctor/appointments/" + appointmentId + "/confirm";
    }

    public static String doctorRejectAppointmentUrl(Long appointmentId) {
        return WEBSITE_BASE_URL + "/doctor/appointments/" + appointmentId + "/reject";
    }
}
