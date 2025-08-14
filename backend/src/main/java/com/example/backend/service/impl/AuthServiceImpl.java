package com.example.backend.service.impl;

import com.example.backend.constant.MessageConstants;
import com.example.backend.constant.enums.Gender;
import com.example.backend.constant.enums.RoleType;
import com.example.backend.dto.request.RegisterRequest;
import com.example.backend.entity.Role;
import com.example.backend.entity.User;
import com.example.backend.exception.UserAlreadyExistsException;
import com.example.backend.repository.RoleRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException(MessageConstants.EMAIL_ALREADY_EXISTS);
        }

        Role patientRole = roleRepository.findByRoleName(RoleType.PATIENT)
                .orElseThrow(() -> new RuntimeException(MessageConstants.ROLE_NOT_FOUND));

        User newUser = User.builder().email(request.getEmail()).fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber()).address(request.getAddress())
                .gender(Gender.valueOf(request.getGender().toUpperCase()))
                .dateOfBirth(LocalDate.parse(request.getDateOfBirth())).isActive(true)
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .roles(Set.of(patientRole)).build();

        return userRepository.save(newUser);
    }
}
