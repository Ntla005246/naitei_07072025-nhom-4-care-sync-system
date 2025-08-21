package com.example.backend.mapper;

import java.util.Optional;

import com.example.backend.dto.UserProfileDto;
import com.example.backend.entity.User;

public final class UserMapper {

    private UserMapper() {}

    public static Optional<UserProfileDto> toUserProfileDto(User user) {
        if (user == null) {
            return Optional.empty();
        }
        return Optional.of(new UserProfileDto(
                user.getFullName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getGender(),
                user.getDateOfBirth(),
                user.getAddress()
        ));
    }
} 
