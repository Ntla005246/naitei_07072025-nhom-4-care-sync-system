package com.example.backend.util;

import java.net.MalformedURLException;
import java.net.URL;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UrlValidator implements ConstraintValidator<ValidUrl, String> {

    @Override
    public void initialize(ValidUrl constraintAnnotation) {
    }

    @Override
    public boolean isValid(String url, ConstraintValidatorContext context) {
        if (url == null || url.trim().isEmpty()) {
            return true;
        }

        try {
            URL parsedUrl = new URL(url);

            String protocol = parsedUrl.getProtocol();
            if (!"http".equals(protocol) && !"https".equals(protocol)) {
                return false;
            }

            String host = parsedUrl.getHost();
            if (host == null || host.trim().isEmpty()) {
                return false;
            }

            if (!host.contains(".")) {
                return false;
            }

            int port = parsedUrl.getPort();
            if (port != -1 && (port < 1 || port > 65535)) {
                return false;
            }

            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }
}
