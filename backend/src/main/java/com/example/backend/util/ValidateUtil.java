package com.example.backend.util;

import java.util.Arrays;
import java.util.List;

public class ValidateUtil {

    private static final List<String> DOCTOR_SORT_FIELDS = Arrays.asList("id", "experienceyears",
            "consultationfee", "title");

    public static String validateDoctorSortBy(String sortBy) {
        if (sortBy == null)
            return "id";

        return DOCTOR_SORT_FIELDS.contains(sortBy.toLowerCase()) ? sortBy : "id";
    }
}
