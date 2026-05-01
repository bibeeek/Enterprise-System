package com.EnterpriseSystem.demo.Controllers;

import com.EnterpriseSystem.demo.Dtos.DepartmentRequestDto;
import com.EnterpriseSystem.demo.Dtos.TaskRequestDto;
import com.EnterpriseSystem.demo.Dtos.TaskResponseDto;
import com.EnterpriseSystem.demo.Dtos.UserResponseDto;
import com.EnterpriseSystem.demo.Exceptions.ApiResponse;
import com.EnterpriseSystem.demo.Services.DepartmentServices;
import com.EnterpriseSystem.demo.Services.TaskServices;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/manager")
@RequiredArgsConstructor
public class ManagerController{


    private final DepartmentServices departmentServices;
    private final TaskServices taskServices;

    @PostMapping("/addDepartment")
    public ResponseEntity<ApiResponse<?>> addDepartment(@RequestBody DepartmentRequestDto departmentRequestDto){

        departmentServices.addDepartment(departmentRequestDto);
        ApiResponse<?> response=new ApiResponse<>("Department Added Successfully",200, LocalDateTime.now(),null);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/assignUserToDepartment/{userId}/{departmentId}")
    public ResponseEntity<ApiResponse<?>> assignUserToDepartment(@PathVariable Long userId  , @PathVariable Long departmentId){

        departmentServices.assignUsersToDepartment(userId,departmentId);
        ApiResponse<?> response=new ApiResponse<>("User Assigned to Department Successfully",200, LocalDateTime.now(),null);
        return ResponseEntity.ok(response);

    }

    @PutMapping("/disableDepartment/{departmentId}")
    public ResponseEntity<ApiResponse<?>> disableDepartment(@PathVariable Long departmentId){

        departmentServices.disableDepartment(departmentId);
        ApiResponse<?> response=new ApiResponse<>("Department Disabled Successfully",200, LocalDateTime.now(),null);
        return ResponseEntity.ok(response);
     }

    @PutMapping("/enableDepartment/{departmentId}")
    public ResponseEntity<ApiResponse<?>> enableDepartment(@PathVariable Long departmentId){

        departmentServices.enableDepartment(departmentId);
        ApiResponse<?> response=new ApiResponse<>("Department Enabled Successfully",200, LocalDateTime.now(),null);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getAllUsersAssignedToDeparment/{departmentId}")
    public ResponseEntity<ApiResponse<List<UserResponseDto>>> getAllUsersAssignedToDepartment(@PathVariable Long departmentId){

        List<UserResponseDto> allUsersByDepartment = departmentServices.getAllUsersByDepartment(departmentId);
        ApiResponse<List<UserResponseDto>> response=new ApiResponse<>("All Users Assigned to Department",200, LocalDateTime.now(),allUsersByDepartment);
        return ResponseEntity.ok(response);

    }

    @PostMapping("/addNewTask/{managerId}")
    public ResponseEntity<ApiResponse<?>> addNewTask(@PathVariable Long managerId, @RequestBody TaskRequestDto taskRequestDto){

        taskServices.addANewTask(managerId,taskRequestDto);
        ApiResponse<?> response=new ApiResponse<>("Task Added Successfully",200, LocalDateTime.now(),null);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getAllTasksAssignedToTheDepartment/{managerId}")
    public ResponseEntity<ApiResponse<List<TaskResponseDto>>> getAllTasksAssignedToManager(@PathVariable Long managerId){

        List<TaskResponseDto> tasksList = taskServices.viewAllTasks(managerId);
        ApiResponse<List<TaskResponseDto>> response=new ApiResponse<>("All Tasks Assigned to This Department",200, LocalDateTime.now(),tasksList);
        return ResponseEntity.ok(response);

    }
    @PutMapping("/assignTaskToEmployees/{managerId}/{userId}/{taskId}")
    public ResponseEntity<ApiResponse<?>> assignTaskToEmployees(@PathVariable Long managerId, @PathVariable Long userId, @PathVariable Long taskId){

        taskServices.assignTaskToUser(managerId,userId,taskId);
        ApiResponse<?> response=new ApiResponse<>("Task Assigned Successfully",200, LocalDateTime.now(),null);
        return ResponseEntity.ok(response);

    }








}
