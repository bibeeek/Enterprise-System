package com.EnterpriseSystem.demo.Services;


import com.EnterpriseSystem.demo.Dtos.DepartmentRequestDto;
import com.EnterpriseSystem.demo.Dtos.UserResponseDto;
import com.EnterpriseSystem.demo.Entity.Departments;
import com.EnterpriseSystem.demo.Entity.Users;
import com.EnterpriseSystem.demo.Repository.DepartmentsRepository;
import com.EnterpriseSystem.demo.Repository.UserRepository;
import com.EnterpriseSystem.demo.Utils.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class DepartmentServices {

    private final DepartmentsRepository departmentsRepository;
    private final UserRepository userRepository;
    private final Mapper mapper;

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
    public void assignUsersToDepartment(Long userId,Long departmentId){

        Optional<Departments> foundDepartment = departmentsRepository.findById(departmentId);


        if (foundDepartment.isEmpty()){
            throw new RuntimeException("Department Does not Exists");
        }

        if (foundDepartment.get().getIsActive() == false){
            throw new RuntimeException("Department is not active");
        }


        Boolean isUserAssigned = userRepository.existsByUserIdAndDepartments(userId,foundDepartment.get());



        if (isUserAssigned){
            throw new RuntimeException("User Already Assigned to this Department :"+" "+foundDepartment.get().getDepartmentName());
        }

        Optional<Users> foundUser = userRepository.findById(userId);
        if (foundUser.isEmpty()){
            throw new RuntimeException("User Does not Exists");
        }
        if (foundUser.get().getIsActive() == false){
            throw new RuntimeException("User is not active");
        }
        if (foundUser.get().getDepartments() != null){
            throw new RuntimeException("User is already assigned to another Department");
        }



        foundUser.get().setDepartments(foundDepartment.get());
        userRepository.save(foundUser.get());

    }

    public void enableDepartment(Long departmentId){
        Departments foundDepartment = departmentsRepository.findById(departmentId).orElseThrow(()->new RuntimeException("Department Does not Exists"));
        foundDepartment.setIsActive(true);
    }

    public void disableDepartment(Long departmentId){
        Departments foundDepartment = departmentsRepository.findById(departmentId).orElseThrow(()->new RuntimeException("Department Does not Exists"));
        foundDepartment.setIsActive(false);
    }

    public List<UserResponseDto> getAllUsersByDepartment(Long departmentId){

        Optional<Departments> fetchedDepartment = departmentsRepository.findById(departmentId);

        if (fetchedDepartment.isEmpty()){
            throw new RuntimeException("Department Does not Exists");
        }

        List<Users> usersInDepartment = userRepository.findAllUsersByDepartments(fetchedDepartment.get());
        if (usersInDepartment.isEmpty()){
            throw new RuntimeException("No Users Found Which Are Assigned to this Department");
        }

        return usersInDepartment.stream().map(mapper::dto).toList();


    }




}
