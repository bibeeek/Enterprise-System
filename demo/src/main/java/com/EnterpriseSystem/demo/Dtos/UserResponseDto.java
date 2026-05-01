package com.EnterpriseSystem.demo.Dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDto {

    private String userName;
    private String phoneNumber;
    private String address;
    private String fullName;
    private String email;
    private String role;
    private Boolean isActive;



}
