package com.example.backend.service.impl;

import com.example.backend.constant.enums.Gender;
import com.example.backend.constant.enums.RoleType;
import com.example.backend.dto.JwtAuthResponseDto;
import com.example.backend.dto.LoginRequestDto;
import com.example.backend.dto.request.RegisterRequest;
import com.example.backend.entity.Role;
import com.example.backend.constant.enums.RoleType;
import com.example.backend.constant.MessageConstants;
import com.example.backend.entity.User;
import com.example.backend.exception.BusinessException;
import com.example.backend.exception.BusinessException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.exception.UnauthorizedException;
import com.example.backend.exception.UserAlreadyExistsException;
import com.example.backend.repository.RoleRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.JwtTokenProvider;
import com.example.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public User register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("error.email.exists");
        }

        Role patientRole = roleRepository.findByRoleName(RoleType.PATIENT)
                .orElseThrow(() -> new BusinessException("error.role.not.found"));

        User newUser = User.builder().email(request.getEmail()).fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber()).address(request.getAddress())
                .gender(Gender.valueOf(request.getGender().toUpperCase()))
                .dateOfBirth(LocalDate.parse(request.getDateOfBirth())).isActive(true)
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .roles(Set.of(patientRole)).build();

        return userRepository.save(newUser);
    }

    // --- VERIFY EMAIL ---
    @Transactional
    @Override
    public void verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstants.TOKEN_INVALID));

        if (user.getTokenExpiryDate().isBefore(LocalDateTime.now())) {
            throw new BusinessException(MessageConstants.TOKEN_EXPIRED_CODE,
                    MessageConstants.TOKEN_EXPIRED_MESSAGE);
        }

        user.setActive(true);
        user.setVerificationToken(null);
        user.setTokenExpiryDate(null);
        userRepository.save(user);
    }

    // --- LOGIN ---
    @Override
    public JwtAuthResponseDto login(LoginRequestDto loginDto) {
        User user = userRepository.findByEmail(loginDto.getEmail()).orElseThrow(
                () -> new UnauthorizedException(MessageConstants.INVALID_EMAIL_OR_PASSWORD));

        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException(MessageConstants.INVALID_EMAIL_OR_PASSWORD);
        }

        String token = jwtTokenProvider.generateToken(user.getEmail());
        return JwtAuthResponseDto.builder().accessToken(token).build();
    }
}
