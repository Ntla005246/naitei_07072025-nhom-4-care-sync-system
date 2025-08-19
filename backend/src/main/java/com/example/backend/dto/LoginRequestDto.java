package com.example.backend.dto;

import com.example.backend.constant.MessageConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDto {
    @NotBlank(message = MessageConstants.EMAIL_NOT_BLANK)
    @Email(message = MessageConstants.EMAIL_INVALID)
    private String email;

    @NotBlank(message = MessageConstants.PASSWORD_NOT_BLANK)
    private String password;
}
