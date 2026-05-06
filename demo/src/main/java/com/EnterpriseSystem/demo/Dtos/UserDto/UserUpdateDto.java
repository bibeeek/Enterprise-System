package com.EnterpriseSystem.demo.Dtos.UserDto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateDto {



    @Pattern(regexp = "^(\\+977)?[0-9]{10}$", message = "Invalid phone number")
    private String phoneNumber;
    private String address;
    private String fullName;


}
