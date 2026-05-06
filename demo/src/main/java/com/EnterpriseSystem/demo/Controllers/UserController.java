package com.EnterpriseSystem.demo.Controllers;

import com.EnterpriseSystem.demo.Dtos.TaskResponseDto;
import com.EnterpriseSystem.demo.Dtos.UserDto.UserLoginDto;
import com.EnterpriseSystem.demo.Dtos.UserDto.UserRegistrationDto;
import com.EnterpriseSystem.demo.Dtos.UserDto.UserResponseDto;
import com.EnterpriseSystem.demo.Dtos.UserDto.UserUpdateDto;
import com.EnterpriseSystem.demo.Exceptions.ApiResponse;
import com.EnterpriseSystem.demo.Services.UserServices;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserServices userServices;

    @GetMapping("/viewUserDetails")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUserDetails(  ){

        UserResponseDto userDetails = userServices.getUserDetails();
        ApiResponse<UserResponseDto> response=new ApiResponse<>("User Details",200, LocalDateTime.now(),userDetails);
        return ResponseEntity.ok(response);

    }

    @PatchMapping("/updateUserDetails")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateUserDetails( @RequestBody @Valid UserUpdateDto userUpdateDto){
        UserResponseDto updatedUserDetails = userServices.updateUserDetails(userUpdateDto);
        ApiResponse<UserResponseDto> response=new ApiResponse<>("User Details Updated",200, LocalDateTime.now(),updatedUserDetails);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/viewAllTasks")
    public ResponseEntity<ApiResponse<List<TaskResponseDto>>> viewAllTasks(){

        List<TaskResponseDto> taskList = userServices.viewUserTasks();
        ApiResponse<List<TaskResponseDto>> response=new ApiResponse<>("All Tasks Assigned to This User",200, LocalDateTime.now(),taskList);
        return ResponseEntity.ok(response);

    }

    @PutMapping("/updateTaskStatusAsCompleted/{taskName}")
    public ResponseEntity<ApiResponse<?>> updateTaskStatus( @PathVariable String taskName){

        userServices.updateTaskStatusAsCompleted(taskName);
        ApiResponse<?> response=new ApiResponse<>("Task Status Updated As Completed",200, LocalDateTime.now(),null);
        return ResponseEntity.ok(response);

    }




}
