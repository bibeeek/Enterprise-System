package com.EnterpriseSystem.demo.Controllers;

import com.EnterpriseSystem.demo.Dtos.UserRequestDto;
import com.EnterpriseSystem.demo.Dtos.UserResponseDto;
import com.EnterpriseSystem.demo.Exceptions.ApiResponse;
import com.EnterpriseSystem.demo.Services.UserServices;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserServices userServices;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> registerUser(@RequestBody @Valid UserRequestDto userRequestDto){

        userServices.registerUser(userRequestDto);

        ApiResponse<?> response= new ApiResponse<>("User Registered Successfully",200, LocalDateTime.now(),null);
        return ResponseEntity.ok(response);

    }


    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserResponseDto>> loginUsers(@RequestBody @Valid  UserRequestDto userRequestDto){

        UserResponseDto loginResponse = userServices.login(userRequestDto);
        ApiResponse<UserResponseDto> response=new ApiResponse<UserResponseDto>("UserLogin Successfully",200, LocalDateTime.now(),loginResponse);
        return ResponseEntity.ok(response);

    }

    @GetMapping("/viewUserDetails/{username}")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUserDetails(@PathVariable String username){

        UserResponseDto userDetails = userServices.getUserDetails(username);
        ApiResponse<UserResponseDto> response=new ApiResponse<>("User Details",200, LocalDateTime.now(),userDetails);
        return ResponseEntity.ok(response);

    }

    @PatchMapping("/updateUserDetails/{username}")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateUserDetails(@PathVariable String username, @RequestBody @Valid UserRequestDto userRequestDto){
        UserResponseDto updatedUserDetails = userServices.updateUserDetails(username,userRequestDto);
        ApiResponse<UserResponseDto> response=new ApiResponse<>("User Details Updated",200, LocalDateTime.now(),updatedUserDetails);
        return ResponseEntity.ok(response);
    }





}
