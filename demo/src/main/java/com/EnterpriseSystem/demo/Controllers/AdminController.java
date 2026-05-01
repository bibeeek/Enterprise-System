package com.EnterpriseSystem.demo.Controllers;


import com.EnterpriseSystem.demo.Dtos.AdminResponseDto;
import com.EnterpriseSystem.demo.Dtos.UserRequestDto;
import com.EnterpriseSystem.demo.Dtos.UserResponseDto;
import com.EnterpriseSystem.demo.Exceptions.ApiResponse;
import com.EnterpriseSystem.demo.Services.AdminServices;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminServices adminServices;

    @PutMapping("/enableUser/{username}/{id}")
    public ResponseEntity<ApiResponse<?>> enableUser(@PathVariable String username, @PathVariable Long id){

        adminServices.enableUser(username,id);
        ApiResponse<String> response=new ApiResponse<>("User Enabled Successfully",200, LocalDateTime.now(),null);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/disableUser/{username}/{id}")
    public ResponseEntity<ApiResponse<?>> disableUser(@PathVariable String username, @PathVariable Long id){

        adminServices.disableUser(username,id);
        ApiResponse<String> response=new ApiResponse<>("User Disabled Successfully",200, LocalDateTime.now(),null);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/addAdmin")
    public ResponseEntity<ApiResponse<?>> addNewAdmin(@RequestBody UserRequestDto userRequestDto) {

        adminServices.addNewAdmin(userRequestDto);
        ApiResponse<String> response=new ApiResponse<>("Admin Added Successfully",200, LocalDateTime.now(),null);
        return ResponseEntity.ok(response);

    }
    @PostMapping("/addManager")
    public ResponseEntity<ApiResponse<?>> addNewManager(@RequestBody UserRequestDto userRequestDto) {

        adminServices.addManager(userRequestDto);
        ApiResponse<String> response=new ApiResponse<>("Manager Added Successfully",200, LocalDateTime.now(),null);
        return ResponseEntity.ok(response);

    }


    @GetMapping("/viewAllUsers")
    public ResponseEntity<ApiResponse<List<AdminResponseDto>>> getAllUsers(){

        List<AdminResponseDto> allActiveUsers = adminServices.getAllActiveUsers();
        ApiResponse<List<AdminResponseDto>> response=new ApiResponse<>("All Active Users",200, LocalDateTime.now(),allActiveUsers);
        return ResponseEntity.ok(response);
    }


}
