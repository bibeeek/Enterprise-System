package com.EnterpriseSystem.demo.Services;


import com.EnterpriseSystem.demo.Dtos.DepartmentRequestDto;
import com.EnterpriseSystem.demo.Dtos.UserDto.UserResponseDto;
import com.EnterpriseSystem.demo.Entity.Departments;
import com.EnterpriseSystem.demo.Entity.Users;
import com.EnterpriseSystem.demo.Repository.DepartmentsRepository;
import com.EnterpriseSystem.demo.Repository.UserRepository;
import com.EnterpriseSystem.demo.Utils.Mapper;
import com.EnterpriseSystem.demo.Utils.Validations;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class DepartmentServices {

    private final DepartmentsRepository departmentsRepository;
    private final UserRepository userRepository;
    private final Mapper mapper;
    private final Validations validations;

    public void addDepartment(DepartmentRequestDto departmentRequestDto){

        Boolean departmentExists = departmentsRepository.existsByDepartmentName(departmentRequestDto.getDepartmentName());
        if (departmentExists) {
            throw new RuntimeException("Department Already Exists");
        }
        Departments departments=Departments.builder().
                 departmentName(departmentRequestDto.getDepartmentName())
                .departmentDescription(departmentRequestDto.getDepartmentDescription())
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();
        departmentsRepository.save(departments);

    }

    public void assignUsersToDepartment(String userName,String departmentName){


        Departments foundDepartment = departmentsRepository.findDepartmentsByDepartmentNameIgnoreCase(departmentName);

        //check if department exists and is active
        validations.validateDepartment(foundDepartment,"Department Does not Exists");
        validations.validateActive(foundDepartment.getIsActive(),"Department is not active");

        Boolean isUserAssigned = userRepository.existsByUserNameAndDepartments(userName,foundDepartment);

        if (isUserAssigned){
            throw new RuntimeException("User Already Assigned to this Department : "+" "+foundDepartment.getDepartmentName());
        }

        Users foundUser = userRepository.findUsersByUserName(userName);

        //check if user exists and is active
        validations.validateUser(foundUser,"User Does not Exists");
        validations.validateActive(foundUser.getIsActive(),"User is not active");
        if (foundUser.getDepartments() != null){
            throw new RuntimeException("User is already assigned to another Department");
        }

        foundUser.setDepartments(foundDepartment);
        userRepository.save(foundUser);

    }

    public void enableDepartment(String departmentName){

        Departments existingDepartment = departmentsRepository.findDepartmentsByDepartmentNameIgnoreCase(departmentName);

        //later add logic to check if the manager is active and belongs to the department

        validations.validateDepartment(existingDepartment,"Department Does not Exists");

        existingDepartment.setIsActive(true);
        departmentsRepository.save(existingDepartment);
    }

    public void disableDepartment(String departmentName){

        Departments existingDepartment = departmentsRepository.findDepartmentsByDepartmentNameIgnoreCase(departmentName);

        List<Users> activeUsers = userRepository.findAllByIsActiveTrue();
        if (!activeUsers.isEmpty()) {
            throw new RuntimeException("Cannot Disable this Department as there are active Users");
        }

        //later add logic to check if the manager is active and belongs to the department

        validations.validateDepartment(existingDepartment,"Department Does not Exists");

        existingDepartment.setIsActive(false);
        departmentsRepository.save(existingDepartment);
    }

    public List<UserResponseDto> listUsersInDepartment(String departmentName){

        Departments departmentDetails = departmentsRepository.findDepartmentsByDepartmentNameIgnoreCase(departmentName);
        validations.validateDepartment(departmentDetails,"Department Does not Exists");
        validations.validateActive(departmentDetails.getIsActive(),"Department is not active");

        List<Users> usersInDepartment = userRepository.findAllUsersByDepartments(departmentDetails);
        if (usersInDepartment.isEmpty()){
            throw new RuntimeException("No Users Assigned to this Department");
        }


        return usersInDepartment.stream().map(mapper::dto).toList();


    }




}
