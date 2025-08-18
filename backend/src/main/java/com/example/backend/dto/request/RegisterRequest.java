package com.example.backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank(message = "{validation.email.required}")
    @Email(message = "{validation.email.invalid}")
    private String email;

    @NotBlank(message = "{validation.password.required}")
    @Size(min = 8, message = "{validation.password.min.length}")
    private String password;

    @NotBlank(message = "{validation.fullname.required}")
    private String fullName;

    private String phoneNumber;
    private String dateOfBirth;
    private String gender;
    private String address;
}
