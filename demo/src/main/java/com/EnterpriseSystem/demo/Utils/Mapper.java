package com.EnterpriseSystem.demo.Utils;

import com.EnterpriseSystem.demo.Authentication.JwtImpl.AuthUtils;
import com.EnterpriseSystem.demo.Dtos.AdminResponseDto;
import com.EnterpriseSystem.demo.Dtos.DepartmentResponseDto;
import com.EnterpriseSystem.demo.Dtos.TaskResponseDto;
import com.EnterpriseSystem.demo.Dtos.UserDto.UserResponseDto;
import com.EnterpriseSystem.demo.Entity.Departments;
import com.EnterpriseSystem.demo.Entity.Tasks;
import com.EnterpriseSystem.demo.Entity.Users;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Mapper {


    public UserResponseDto dto(Users user){

        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setUserName(user.getUserName());
        userResponseDto.setEmail(user.getEmail());
        userResponseDto.setRole(user.getRole().toString());
        userResponseDto.setIsActive(user.getIsActive());
        userResponseDto.setAddress(user.getAddress());
        userResponseDto.setFullName(user.getFullName());
        userResponseDto.setPhoneNumber(user.getPhoneNumber());
        userResponseDto.setLastLogin(user.getLastLogin()==null?null:user.getLastLogin().toString());
        userResponseDto.setDepartmentName(user.getDepartments()==null?null:user.getDepartments().getDepartmentName());

        return userResponseDto;


    }


    public AdminResponseDto adminResponseDto(Users user){

        AdminResponseDto adminResponse = new AdminResponseDto();
        adminResponse.setId(user.getUserId());
        adminResponse.setUserName(user.getUserName());
        adminResponse.setEmail(user.getEmail());
        adminResponse.setRole(user.getRole().toString());
        adminResponse.setIsActive(user.getIsActive());
        adminResponse.setAddress(user.getAddress());
        adminResponse.setPhoneNumber(user.getPhoneNumber());
        adminResponse.setCreatedAt(user.getCreatedAt().toString());
        adminResponse.setFullName(user.getFullName());

        adminResponse.setLastLogin(
                user.getLastLogin() != null ? user.getLastLogin().toString() : null
        );

        adminResponse.setDepartmentName(
                user.getDepartments() != null ? user.getDepartments().getDepartmentName() : null
        );

        adminResponse.setUpdatedAt(
                user.getUpdatedAt() != null ? user.getUpdatedAt().toString() : null
        );

        return adminResponse;


    }

    public TaskResponseDto taskResponseDto(Tasks tasks){

        TaskResponseDto taskResponseDto=new TaskResponseDto();
        taskResponseDto.setTaskId(tasks.getTaskId());
        taskResponseDto.setIsActive(tasks.getIsActive());
        taskResponseDto.setTaskName(tasks.getTaskName());
        taskResponseDto.setTaskDescription(tasks.getTaskDescription());
        taskResponseDto.setTaskStatus(tasks.getTaskStatus().toString());
        taskResponseDto.setTaskPriority(tasks.getTaskPriority().toString());
        taskResponseDto.setTaskCategory(tasks.getTaskCategory().toString());
        taskResponseDto.setDeadline(tasks.getDeadline().toString());
        taskResponseDto.setEstimatedTimeInHrs(tasks.getEstimatedTimeInHrs());
        taskResponseDto.setCreatedAt(tasks.getCreatedAt().toString());
        taskResponseDto.setCreatedBy(tasks.getCreatedBy().getUserName());
        taskResponseDto.setDepartmentName(tasks.getCreatedBy().getDepartments().getDepartmentName());
        taskResponseDto.setUpdatedAt(tasks.getUpdatedAt()==null?null:tasks.getUpdatedAt().toString());
        taskResponseDto.setAssignedTo(tasks.getAssignedTo()==null?null:tasks.getAssignedTo().getUserName());

        return taskResponseDto;


    }

    public DepartmentResponseDto departmentResponseDto(Departments departments){

        DepartmentResponseDto departmentResponseDto= new DepartmentResponseDto();

        departmentResponseDto.setDepartmentName(departments.getDepartmentName());
        departmentResponseDto.setDepartmentDescription(departments.getDepartmentDescription());
        departmentResponseDto.setIsActive(departments.getIsActive());
        if (departments.getUsers() != null) {
            List<String> managers = departments.getUsers().stream()
                    .filter(u -> u.getRole() == Roles.ROLE_MANAGER)
                    .map(Users::getUserName)
                    .toList();
            departmentResponseDto.setManagersUserNames(managers);
        }
        if (departments.getUsers() != null) {
            departments.getUsers().forEach(u ->
                    System.out.println("User: " + u.getUserName() + " Role: " + u.getRole())
            );
        }
        departmentResponseDto.setCreatedAt(departments.getCreatedAt().toString());
        return departmentResponseDto;


    }

}
