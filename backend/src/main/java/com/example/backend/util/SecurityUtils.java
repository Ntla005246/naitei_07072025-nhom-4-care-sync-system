package com.example.backend.util;

import com.example.backend.entity.User;
import com.example.backend.exception.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public final class SecurityUtils {
    private SecurityUtils() {
    }

    public static String getCurrentUserEmailOrThrow() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedException("error.unauthorized");
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof UserDetails ud) {
            return ud.getUsername();
        }
        if (principal instanceof String s) {
            if ("anonymousUser".equalsIgnoreCase(s)) {
                throw new UnauthorizedException("error.unauthorized");
            }
            return s;
        }
        if (principal instanceof User u) {
            return u.getEmail();
        }
        throw new UnauthorizedException("error.unauthorized");
    }
}
