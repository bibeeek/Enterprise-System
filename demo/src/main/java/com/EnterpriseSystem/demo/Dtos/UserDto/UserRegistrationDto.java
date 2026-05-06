package com.EnterpriseSystem.demo.Dtos;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserRegistrationDto {

    @NotBlank(message = "User Name is required")
    private String userName;

    @Email(message = "Please enter a valid email",regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    @Pattern(
            regexp = "^(?=.*[0-9]).{8,}$",
            message = "Password must be at least 8 characters long and contain at least one number"
    )
    private String passWord;

    @NotBlank(message = "Full Name is required")
    private String fullName;

    private LocalDateTime createdAt;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Phone Number is required")
    @Pattern(regexp = "^(\\+977)?[0-9]{10}$", message = "Invalid phone number")
    private String phoneNumber;


}
