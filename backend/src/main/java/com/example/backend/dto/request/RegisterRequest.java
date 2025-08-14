package com.example.backend.dto.request;

import com.example.backend.constant.MessageConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank(message = MessageConstants.EMAIL_REQUIRED)
    @Email(message = MessageConstants.EMAIL_INVALID)
    private String email;

    @NotBlank(message = MessageConstants.PASSWORD_REQUIRED)
    @Size(min = 8, message = MessageConstants.PASSWORD_MIN_LENGTH)
    private String password;

    @NotBlank(message = MessageConstants.FULLNAME_REQUIRED)
    private String fullName;

    private String phoneNumber;
    private String dateOfBirth;
    private String gender;
    private String address;
}
