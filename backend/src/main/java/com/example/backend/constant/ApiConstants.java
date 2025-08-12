package com.example.backend.constant;

public final class ApiConstants {
    private ApiConstants() {
    }

    private static final String API_V1 = "/api/v1";

    public static final String AUTH_ENDPOINT = API_V1 + "/auth";
    public static final String USERS_ENDPOINT = API_V1 + "/users";
    public static final String APPOINTMENTS_ENDPOINT = API_V1 + "/appointments";
    public static final String DOCTORS_ENDPOINT = API_V1 + "/doctors";
    public static final String ADMIN_ENDPOINT = API_V1 + "/admin";
}
