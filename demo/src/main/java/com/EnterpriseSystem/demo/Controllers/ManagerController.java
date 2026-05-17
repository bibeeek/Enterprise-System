package com.EnterpriseSystem.demo.Controllers;

import com.EnterpriseSystem.demo.Dtos.DepartmentRequestDto;
import com.EnterpriseSystem.demo.Dtos.DepartmentResponseDto;
import com.EnterpriseSystem.demo.Dtos.TaskRequestDto;
import com.EnterpriseSystem.demo.Dtos.TaskResponseDto;
import com.EnterpriseSystem.demo.Dtos.UserDto.UserResponseDto;
import com.EnterpriseSystem.demo.Exceptions.ApiResponse;
import com.EnterpriseSystem.demo.Services.ManagerServices;
import com.EnterpriseSystem.demo.Services.TaskServices;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/manager")
@RequiredArgsConstructor
public class ManagerController{


    private final ManagerServices managerServices;
    private final TaskServices taskServices;



    @PostMapping("/assignUserToDepartment/{userName}/{departmentName}")
    public ResponseEntity<ApiResponse<?>> assignUserToDepartment(@PathVariable String userName  , @PathVariable String departmentName){

        managerServices.assignUsersToDepartment(userName,departmentName);
        ApiResponse<?> response=new ApiResponse<>("User Assigned to Department Successfully",200, LocalDateTime.now(),null);
        return ResponseEntity.ok(response);

    }



    @GetMapping("/getAllUsersAssignedToDeparment/{departmentName}")
    public ResponseEntity<ApiResponse<List<UserResponseDto>>> getAllUsersAssignedToDepartment(@PathVariable String departmentName){

        List<UserResponseDto> allUsersByDepartment = managerServices.listUsersInDepartment(departmentName);
        ApiResponse<List<UserResponseDto>> response=new ApiResponse<>("All Users Assigned to Department",200, LocalDateTime.now(),allUsersByDepartment);
        return ResponseEntity.ok(response);

    }


    @PostMapping("/addNewTask")
    public ResponseEntity<ApiResponse<?>> addNewTask( @RequestBody @Valid TaskRequestDto taskRequestDto){

        taskServices.addANewTask(taskRequestDto);
        ApiResponse<?> response=new ApiResponse<>("Task Added Successfully",200, LocalDateTime.now(),null);
        return ResponseEntity.ok(response);
    }



    @GetMapping("/getAllTasksAssignedToTheDepartment")
    public ResponseEntity<ApiResponse<List<TaskResponseDto>>> getAllTasksAssignedToManager(){

        List<TaskResponseDto> tasksList = taskServices.viewAllTasks();
        ApiResponse<List<TaskResponseDto>> response=new ApiResponse<>("All Tasks Assigned to This Department",200, LocalDateTime.now(),tasksList);
        return ResponseEntity.ok(response);

    }
    @PutMapping("/assignTaskToEmployees/{EmployeeUserName}/{taskName}")
    public ResponseEntity<ApiResponse<?>> assignTaskToEmployees( @PathVariable String EmployeeUserName, @PathVariable String taskName){

        taskServices.assignTaskToUser(EmployeeUserName,taskName);
        ApiResponse<?> response=new ApiResponse<>("Task Assigned Successfully",200, LocalDateTime.now(),null);
        return ResponseEntity.ok(response);

    }

    @GetMapping("/viewTaskCountOfUser/{userName}")
    public ResponseEntity<ApiResponse<Long>> viewTaskCountOfUser(@PathVariable String userName){

        long taskCount = taskServices.countTaskPerUser(userName);
        ApiResponse<Long> response=new ApiResponse<>("Task Count of User",200, LocalDateTime.now(),taskCount);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/updateTaskStatusAsCancelled/{userName}/{taskName}")
    public ResponseEntity<ApiResponse<?>> updateTaskStatus(@PathVariable String userName, @PathVariable String taskName){

        managerServices.updateTaskStatusAsCancelled(userName,taskName);
        ApiResponse<?> response=new ApiResponse<>("Task Status Updated As Cancelled",200, LocalDateTime.now(),null);
        return ResponseEntity.ok(response);


    }

    @PutMapping("reassignTaskToAnotherUser/{userName}/{taskName}/{targetUserName}")
    public ResponseEntity<ApiResponse<?>> reassignTaskToAnotherUser( @PathVariable String userName, @PathVariable String taskName, @PathVariable String targetUserName){

        managerServices.reassignTaskToUser(userName,taskName,targetUserName);
        ApiResponse<?> response=new ApiResponse<>("Task Reassigned Successfully",200, LocalDateTime.now(),null);
        return ResponseEntity.ok(response);

    }

    @PutMapping("/disableTask/{taskName}")
    public ResponseEntity<ApiResponse<?>> disableTask(@PathVariable String taskName){

        taskServices.disableTask(taskName);
        ApiResponse<?> response=new ApiResponse<>("Task Disabled Successfully",200, LocalDateTime.now(),null);
        return ResponseEntity.ok(response);

    }
    @PutMapping("/enableTask/{taskName}")
    public ResponseEntity<ApiResponse<?>> enableTask(@PathVariable String taskName){

        taskServices.enableTask(taskName);
        ApiResponse<?> response=new ApiResponse<>("Task Enabled Successfully",200, LocalDateTime.now(),null);
        return ResponseEntity.ok(response);
    }

    //newly added methods
    @GetMapping("/getMyDepartment")
    public ResponseEntity<ApiResponse<DepartmentResponseDto>> getMyDepartment() {
        DepartmentResponseDto dept = managerServices.getManagerDepartment();
        ApiResponse<DepartmentResponseDto> response = new ApiResponse<>("Manager Department", 200, LocalDateTime.now(), dept);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getMyDepartmentUsers")
    public ResponseEntity<ApiResponse<List<UserResponseDto>>> getMyDepartmentUsers() {
        List<UserResponseDto> users = managerServices.listUsersInManagerDepartment();
        ApiResponse<List<UserResponseDto>> response = new ApiResponse<>("Department Users", 200, LocalDateTime.now(), users);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getMyDepartmentManagers")
    public ResponseEntity<ApiResponse<List<UserResponseDto>>> getMyDepartmentManagers(){

        List<UserResponseDto> users= managerServices.listManagerInDepartment();
        ApiResponse<List<UserResponseDto>> response=new ApiResponse<>("Department Managers",200, LocalDateTime.now(),users);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getAllUnassignedUsers")
    public ResponseEntity<ApiResponse<List<UserResponseDto>>> getAllUnassignedUsers(){

        List<UserResponseDto> unassignedUsers = managerServices.listUnassignedUsers();
        ApiResponse<List<UserResponseDto>> response=new ApiResponse<>("All Unassigned Users",200, LocalDateTime.now(),unassignedUsers);
        return ResponseEntity.ok(response);

    }
    @PutMapping("/unassignUserFromDepartment/{username}")
    public ResponseEntity<ApiResponse<?>> unassignUserFromDepartment(@PathVariable String username){

        managerServices.unassignUser(username);
        ApiResponse<?> response=new ApiResponse<>("User Unassigned Successfully",200, LocalDateTime.now(),null);
        return ResponseEntity.ok(response);

    }









}
