package com.EnterpriseSystem.demo.Services;

import com.EnterpriseSystem.demo.Dtos.AdminResponseDto;
import com.EnterpriseSystem.demo.Dtos.DepartmentResponseDto;
import com.EnterpriseSystem.demo.Dtos.UserDto.UserRegistrationDto;
import com.EnterpriseSystem.demo.Entity.AuditLogs;
import com.EnterpriseSystem.demo.Entity.Departments;
import com.EnterpriseSystem.demo.Entity.Users;
import com.EnterpriseSystem.demo.Exceptions.CustomExceptions.UserAlreadyExistsException;
import com.EnterpriseSystem.demo.Repository.AuditLogsRepository;
import com.EnterpriseSystem.demo.Repository.DepartmentsRepository;
import com.EnterpriseSystem.demo.Repository.UserRepository;
import com.EnterpriseSystem.demo.Utils.Mapper;
import com.EnterpriseSystem.demo.Utils.Roles;
import com.EnterpriseSystem.demo.Utils.Validations;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServices {

    private final UserRepository userRepository;
    private final Mapper mapper;
    private final AuditLogsRepository auditLogsRepository;
    private final Validations validations;
    private final DepartmentsRepository departmentsRepository;
    private final PasswordEncoder passwordEncoder;

    public void addNewAdmin(UserRegistrationDto userRegistrationDto) {

        boolean b = userRepository.existsByUserName(userRegistrationDto.getUserName());
        Boolean emailExists = userRepository.existsByEmail(userRegistrationDto.getEmail());

        if (emailExists) {
            throw new UserAlreadyExistsException("User Already Exists with this email");
        }
        if (b) {
            throw new RuntimeException("A User Already Exists with this Username");
        }

        String securedPassword = passwordEncoder.encode(userRegistrationDto.getPassWord());

        try {
            Users user = Users.builder()
                    .userName(userRegistrationDto.getUserName())
                    .passWord(securedPassword)
                    .email(userRegistrationDto.getEmail())
                    .fullName(userRegistrationDto.getFullName())
                    .role(Roles.ROLE_ADMIN)
                    .createdAt(LocalDateTime.now())
                    .phoneNumber(userRegistrationDto.getPhoneNumber())
                    .address(userRegistrationDto.getAddress())
                    .isActive(true)
                    .accountNonLocked(true)
                    .failedLoginAttempts(0)
                    .build();

            userRepository.save(user);

            AuditLogs logs = AuditLogs.builder().
                    action("Added New Admin")
                    .performedBy("Admin")
                    .targetEntity("User :" + userRegistrationDto.getUserName())
                    .timestamp(LocalDateTime.now()).build();
            auditLogsRepository.save(logs);


        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public void addManager(UserRegistrationDto userRegistrationDto) {

        Boolean emailExists = userRepository.existsByEmail(userRegistrationDto.getEmail());
        boolean b = userRepository.existsByUserName(userRegistrationDto.getUserName());
        if (b) {
            throw new RuntimeException("A User Already Exists with this Username");
        }
        if (emailExists) {
            throw new UserAlreadyExistsException("User Already Exists with this email");
        }
        String securedPassword = passwordEncoder.encode(userRegistrationDto.getPassWord());
        try {
            Users user = Users.builder()
                    .userName(userRegistrationDto.getUserName())
                    .passWord(securedPassword)
                    .email(userRegistrationDto.getEmail())
                    .fullName(userRegistrationDto.getFullName())
                    .phoneNumber(userRegistrationDto.getPhoneNumber())
                    .address(userRegistrationDto.getAddress())
                    .role(Roles.ROLE_MANAGER)
                    .createdAt(LocalDateTime.now())
                    .isActive(true)
                    .accountNonLocked(true)
                    .failedLoginAttempts(0)
                    .build();

            userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        AuditLogs logs = AuditLogs.builder().
                action("Added New Manager")
                .performedBy("Admin")
                .targetEntity("Manager :" + userRegistrationDto.getUserName())
                .timestamp(LocalDateTime.now()).build();
        auditLogsRepository.save(logs);

    }


    public void disableUser(String userName) {

        Users user = userRepository.findUsersByUserName(userName);
        if (user == null) {
            throw new RuntimeException("User Does not Exists");
        }
        if (user.getRole() == Roles.ROLE_ADMIN || user.getRole() == Roles.ROLE_MANAGER) {
            throw new RuntimeException("Cannot Disable this User as it is a Manager or Admin");
        }
        user.setIsActive(false);
        userRepository.save(user);
        AuditLogs logs = AuditLogs.builder().
                action("Disabled User")
                .performedBy("Admin")
                .targetEntity("User :" + user.getUserName())
                .timestamp(LocalDateTime.now()).build();
        auditLogsRepository.save(logs);
    }

    public void enableUser(String userName) {


        Users user = userRepository.findUsersByUserName(userName);
        if (user == null) {
            throw new RuntimeException("User Does not Exists");
        }
        if (user.getRole() == Roles.ROLE_ADMIN || user.getRole() == Roles.ROLE_MANAGER) {
            throw new RuntimeException("Cannot Enable this User as it is a Manager or Admin");
        }
        user.setIsActive(true);
        userRepository.save(user);

        AuditLogs logs = AuditLogs.builder().
                action("Enabled User")
                .performedBy("Admin")
                .targetEntity("User :" + user.getUserName())
                .timestamp(LocalDateTime.now()).build();
        auditLogsRepository.save(logs);

    }

    public void disableManager(String userName) {

        Users manager = userRepository.findUsersByUserName(userName);

        validations.validateUser(manager, "Cannot find Manager With This UserName");
        validations.validateRole(manager.getRole(), Roles.ROLE_MANAGER, "This User is not a Manager, Cannot Disable a Manager");

        manager.setIsActive(false);
        userRepository.save(manager);
        AuditLogs logs = AuditLogs.builder().
                action("Disabled Manager")
                .performedBy("Admin")
                .targetEntity("Manager :" + manager.getUserName())
                .timestamp(LocalDateTime.now()).build();
        auditLogsRepository.save(logs);


    }

    public void enableManager(String userName) {

        Users manager = userRepository.findUsersByUserName(userName);

        validations.validateUser(manager, "Cannot find Manager With This UserName");
        validations.validateRole(manager.getRole(), Roles.ROLE_MANAGER, "This User is not a Manager, Cannot Enable a Manager");

        manager.setIsActive(true);
        userRepository.save(manager);
        AuditLogs logs = AuditLogs.builder().
                action("Enabled Manager")
                .performedBy("Admin")
                .targetEntity("Manager :" + manager.getUserName())
                .timestamp(LocalDateTime.now()).build();
        auditLogsRepository.save(logs);

    }


    public void assignManagersToDepartment(String managerUserName, String departmentName) {


        Departments foundDepartment = departmentsRepository.findDepartmentsByDepartmentNameIgnoreCase(departmentName);

        //check if department exists and is active
        validations.validateDepartment(foundDepartment, "Department Does not Exists");
        validations.validateActive(foundDepartment.getIsActive(), "Department is not active");

        Boolean isUserAssigned = userRepository.existsByUserNameAndDepartments(managerUserName, foundDepartment);


        if (isUserAssigned) {
            throw new RuntimeException("User Already Assigned to this Department : " + " " + foundDepartment.getDepartmentName());
        }

        Users foundUser = userRepository.findUsersByUserName(managerUserName);

        //check if user exists and is active
        validations.validateUser(foundUser, "Manager Does not Exists");
        validations.validateActive(foundUser.getIsActive(), "Manager is not active");
        validations.validateRole(foundUser.getRole(), Roles.ROLE_MANAGER, "This User is not a Manager, Cannot Assign to a Department");

        if (foundUser.getDepartments() != null) {
            throw new RuntimeException("Manager is already assigned to another Department");
        }

        foundUser.setDepartments(foundDepartment);
        userRepository.save(foundUser);
        AuditLogs logs = AuditLogs.builder().
                action("Assigned Manager to Department")
                .performedBy("Manager")
                .targetEntity("User :" + managerUserName + " Department :" + foundDepartment.getDepartmentName())
                .timestamp(LocalDateTime.now()).build();
        auditLogsRepository.save(logs);

    }


    public List<AdminResponseDto> getAllActiveUsers(int page, int size) {

        List<Users> activeUserList = userRepository.findAllByIsActiveTrueAndRole(Roles.ROLE_USER, Pageable.ofSize(size).withPage(page));


        if (activeUserList.isEmpty()) {
            throw new RuntimeException("No Active Users Found");
        }

        return activeUserList.stream().map(mapper::adminResponseDto).toList();


    }
    public List<AdminResponseDto> getAllActiveManagers(int page, int size) {

        List<Users> activeManagerList = userRepository.findAllByIsActiveTrueAndRole(Roles.ROLE_MANAGER,Pageable.ofSize(size).withPage(page));

        if (activeManagerList.isEmpty()) {
            throw new RuntimeException("No Active Managers Found");
        }

        return activeManagerList.stream().map(mapper::adminResponseDto).toList();
    }
    public List<AdminResponseDto> getAllActiveAdmins(int page, int size) {

        List<Users> activeAdminList = userRepository.findAllByIsActiveTrueAndRole(Roles.ROLE_ADMIN,Pageable.ofSize(size).withPage(page));

        if (activeAdminList.isEmpty()) {
            throw new RuntimeException("No Active Admins Found");
        }

        return activeAdminList.stream().map(mapper::adminResponseDto).toList();
    }
    public List<AdminResponseDto> getAllUsers(int page, int size) {
        List<Users> allUsers=userRepository.findAllByRole(Roles.ROLE_USER);
        if (allUsers.isEmpty()) {
            throw new RuntimeException("No Users Found");
        }
        return allUsers.stream().map(mapper::adminResponseDto).toList();
    }
    public List<AdminResponseDto> getAllManagers(int page, int size) {
        List<Users> allManagers=userRepository.findAllByRole(Roles.ROLE_MANAGER);
        if (allManagers.isEmpty()) {
            throw new RuntimeException("No Managers Found");
        }
        return allManagers.stream().map(mapper::adminResponseDto).toList();
    }
    public List<AdminResponseDto> getAllAdmins(int page, int size) {
        List<Users> allAdmins=userRepository.findAllByRole(Roles.ROLE_ADMIN);
        if (allAdmins.isEmpty()) {
            throw new RuntimeException("No Admins Found");
        }
        return allAdmins.stream().map(mapper::adminResponseDto).toList();
    }


    @Transactional
    public List<DepartmentResponseDto> viewAllDepartments(){

        List<Departments> allDepartments = departmentsRepository.findAll();

        if (allDepartments.isEmpty()) {
            throw new RuntimeException("No Departments Found");
        }

        return allDepartments.stream().map(mapper::departmentResponseDto).toList();



        }

    }




