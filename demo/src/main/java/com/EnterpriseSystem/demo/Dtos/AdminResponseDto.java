package com.EnterpriseSystem.demo.Dtos;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminResponseDto {


    private Long id;
    private String userName;
    private String email;
    private String role;
    private Boolean isActive;
    private String departmentName;
    private Long phoneNumber;
    private String address;
    private String lastLogin;
    private String createdAt;
    private String updatedAt;

}
