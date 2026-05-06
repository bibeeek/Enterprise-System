package com.EnterpriseSystem.demo.Dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateDto {

    private String userName;

    @Pattern(regexp = "^(\\+977)?[0-9]{10}$", message = "Invalid phone number")
    private String phoneNumber;
    private String address;
    private String fullName;
    @Email(message = "Please enter a valid email",regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}")
    private String email;

    @Pattern(
            regexp = "^(?=.*[0-9]).{8,}$",
            message = "Password must be at least 8 characters long and contain at least one number"
    )
    private String passWord;

}
