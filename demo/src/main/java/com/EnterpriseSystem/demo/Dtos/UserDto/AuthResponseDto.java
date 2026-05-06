package com.EnterpriseSystem.demo.Dtos.UserDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AuthResponseDto {


    private UserResponseDto userResponseDto;
    private String jwtToken;
}
