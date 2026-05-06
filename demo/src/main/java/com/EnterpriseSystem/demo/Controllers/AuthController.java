package com.EnterpriseSystem.demo.Controllers;

import com.EnterpriseSystem.demo.Dtos.UserDto.AuthResponseDto;
import com.EnterpriseSystem.demo.Dtos.UserDto.UserLoginDto;
import com.EnterpriseSystem.demo.Dtos.UserDto.UserRegistrationDto;
import com.EnterpriseSystem.demo.Dtos.UserDto.UserResponseDto;
import com.EnterpriseSystem.demo.Exceptions.ApiResponse;
import com.EnterpriseSystem.demo.Services.UserServices;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserServices userServices;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDto>> loginUsers(@RequestBody @Valid UserLoginDto userLoginDto){

        AuthResponseDto loginResponse = userServices.login(userLoginDto);
        ApiResponse<AuthResponseDto> response=new ApiResponse<>("UserLogin Successfully",200, LocalDateTime.now(),loginResponse);
        return ResponseEntity.ok(response);

    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> registerUser(@RequestBody @Valid UserRegistrationDto userRegistrationDto){

        userServices.registerUser(userRegistrationDto);

        ApiResponse<?> response= new ApiResponse<>("User Registered Successfully",200, LocalDateTime.now(),null);
        return ResponseEntity.ok(response);

    }
}
