package com.EnterpriseSystem.demo.Dtos;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserRequestDto {

    @NotBlank(message = "User Name is required")
    private String userName;

    @Email(message = "Please enter a valid email",regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String passWord;

    @NotBlank(message = "Full Name is required")
    private String fullName;

    private LocalDateTime createdAt;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Phone Number is required")
    private Long phoneNumber;


}
