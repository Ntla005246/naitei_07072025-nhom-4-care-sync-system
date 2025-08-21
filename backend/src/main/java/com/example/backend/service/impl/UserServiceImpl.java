package com.example.backend.service.impl;

import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.dto.UserProfileDto;
import com.example.backend.entity.User;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.exception.UnauthorizedException;
import com.example.backend.mapper.UserMapper;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.JwtTokenProvider;
import com.example.backend.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final MessageSource messageSource;
    private final JwtTokenProvider jwtTokenProvider;
    private final HttpServletRequest httpServletRequest;

    @Override
    public UserProfileDto getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String candidateEmail = (authentication != null) ? authentication.getName() : null;

        if (candidateEmail == null || "anonymousUser".equalsIgnoreCase(candidateEmail)) {
            String bearer = httpServletRequest.getHeader("Authorization");
            if (bearer == null || !bearer.startsWith("Bearer ")) {
                throw new UnauthorizedException("error.unauthorized");
            }
            String token = bearer.substring(7);
            if (!jwtTokenProvider.validateToken(token)) {
                throw new UnauthorizedException("error.unauthorized");
            }
            candidateEmail = jwtTokenProvider.getUsernameFromToken(token);
        }

        final String lookupEmail = candidateEmail;

        User user = userRepository.findByEmail(lookupEmail)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "error.user.not.found.by.email", lookupEmail));
        return UserMapper.toUserProfileDto(user)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "error.user.not.found.by.email", lookupEmail));
    }

} 
