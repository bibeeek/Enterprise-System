package com.EnterpriseSystem.demo.Services;

import com.EnterpriseSystem.demo.Authentication.CustomUser;
import com.EnterpriseSystem.demo.Authentication.JwtImpl.AuthUtils;
import com.EnterpriseSystem.demo.Dtos.TaskResponseDto;
import com.EnterpriseSystem.demo.Dtos.UserDto.*;
import com.EnterpriseSystem.demo.Entity.AuditLogs;
import com.EnterpriseSystem.demo.Entity.Tasks;
import com.EnterpriseSystem.demo.Entity.Users;
import com.EnterpriseSystem.demo.Exceptions.CustomExceptions.*;
import com.EnterpriseSystem.demo.Repository.AuditLogsRepository;
import com.EnterpriseSystem.demo.Repository.TasksRepository;
import com.EnterpriseSystem.demo.Repository.UserRepository;
import com.EnterpriseSystem.demo.Utils.Mapper;
import com.EnterpriseSystem.demo.Utils.Roles;
import com.EnterpriseSystem.demo.Utils.TaskStatus;
import com.EnterpriseSystem.demo.Utils.Validations;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;


@Service
@RequiredArgsConstructor
public class UserServices {

    private final UserRepository userRepository;

    private final Validations validations;
    private final Mapper mapper;
    private final TasksRepository tasksRepository;
    private final AuditLogsRepository auditLogsRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final AuthUtils authUtils;

    private final AccountSecurityServices accountSecurityServices;


    public void registerUser(UserRegistrationDto userRegistrationDto) {

        boolean userExists = userRepository.existsByUserName((userRegistrationDto.getUserName()));
        Boolean emailExists = userRepository.existsByEmail(userRegistrationDto.getEmail());
        if (emailExists) {
            throw new UserAlreadyExistsException("User Already Exists with this email");
        }
        if (userExists) {
            throw new UserAlreadyExistsException("User Already Exists with this username");
        }
        String encodedPassword = passwordEncoder.encode(userRegistrationDto.getPassWord());

        try {
            Users user = Users.builder()
                    .userName(userRegistrationDto.getUserName())
                    .passWord(encodedPassword)
                    .email(userRegistrationDto.getEmail())
                    .role(Roles.ROLE_USER)
                    .createdAt(LocalDateTime.now())
                    .phoneNumber(userRegistrationDto.getPhoneNumber())
                    .address(userRegistrationDto.getAddress())
                    .fullName(userRegistrationDto.getFullName())
                    .isActive(true)
                    .accountNonLocked(true)
                    .failedLoginAttempts(0)
                    .build();

            userRepository.save(user);

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        AuditLogs logs = AuditLogs.builder().
                action("Registered New User")
                .performedBy("User")
                .targetEntity("User :" + userRegistrationDto.getUserName())
                .timestamp(LocalDateTime.now()).build();
        auditLogsRepository.save(logs);

    }

    public AuthResponseDto login(UserLoginDto dto) {


        Users users = userRepository.findUsersByEmail(dto.getEmail());
        if (users == null) {
            throw new ResourceNotFoundException("User Not Fount With  Email: "+" "+dto.getEmail());
        }
        validations.validateActive(users.getIsActive(), "User is not active");

        accountSecurityServices.checkAndUnlockAccount(users);
        if (!users.getAccountNonLocked()) {

            throw new AccountLockedException("Account is locked due to multiple failed attempts. Try again later.");
        }


        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            dto.getEmail(),
                            dto.getPassWord()
                    )
            );

            CustomUser userDetails = (CustomUser) authentication.getPrincipal();

            Users user = userDetails.getUsers();

            String token = authUtils.generateToken(user);

            UserResponseDto responseDto = mapper.dto(user);

            accountSecurityServices.handleSuccessfulLoginAttempt(user);

            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);

            return new AuthResponseDto(responseDto, token);

        } catch (Exception e) {

            accountSecurityServices.handleFailedLoginAttempt(users);
            throw new UnauthorizedException("Invalid email or password") {
            };
        }
    }


    public UserResponseDto getUserDetails() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Users user = userRepository.findUsersByEmail(email);

        if (user == null) {
            throw new ResourceNotFoundException("User Not Found");
        }

        return mapper.dto(user);

    }

    public UserResponseDto updateUserDetails(UserUpdateDto userUpdateDto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Users foundUser = userRepository.findUsersByEmail(email);


        if (foundUser == null) {
            throw new ResourceNotFoundException("User Not Found");
        }

//            if (foundUser.getIsActive() == false){
//                throw new RuntimeException("Cannot Update :: User is not active");
//            }

        if (userUpdateDto.getAddress() != null) {
            foundUser.setAddress(userUpdateDto.getAddress());
        }
        if (userUpdateDto.getPhoneNumber() != null) {
            foundUser.setPhoneNumber(userUpdateDto.getPhoneNumber());
        }
        if (userUpdateDto.getFullName() != null) {
            foundUser.setFullName(userUpdateDto.getFullName());
        }

        foundUser.setUpdatedAt(LocalDateTime.now());
        userRepository.save(foundUser);

        AuditLogs logs = AuditLogs.builder()
                .action("User Update")
                .performedBy("User:" + foundUser.getUserName())
                .targetEntity("User " + foundUser.getUserName())
                .timestamp(LocalDateTime.now()).build();
        auditLogsRepository.save(logs);

        return mapper.dto(foundUser);


    }

    public List<TaskResponseDto> viewUserTasks() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Users foundUser = userRepository.findUsersByEmail(email);


        validations.validateUser(foundUser, "User Does not Exist");
        validations.validateDepartment(foundUser.getDepartments(), "User is not assigned to any Department, enroll in a Department to view tasks");
        validations.validateRole(foundUser.getRole(), Roles.ROLE_USER, "This User Does Not Have Access to View Tasks");

        List<Tasks> userAssignedTasks = tasksRepository.findAllByAssignedTo(foundUser);


        if (userAssignedTasks.isEmpty()) {
            throw new ResourceNotFoundException("No Tasks Assigned to this User");
        }
        return userAssignedTasks.stream().map(mapper::taskResponseDto).toList();


    }

    public void updateTaskStatusAsCompleted(String taskName) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Users user = userRepository.findUsersByEmail(email);


        validations.validateUser(user, "User Does not Exist");
        validations.validateActive(user.getIsActive(), "User is not active");
        validations.validateDepartment(user.getDepartments(), "User is not assigned to any Department, enroll in a Department to update tasks");
        validations.validateRole(user.getRole(), Roles.ROLE_USER, "This User does not have user Role, Cannot update the Task");

        Tasks foundTask = tasksRepository.findByTaskName(taskName);

        if (foundTask == null) {
            throw new ResourceNotFoundException("Task Does not Exist");
        }
        validations.validateActive(foundTask.getIsActive(), "Task is not active");

        if (!Objects.equals(foundTask.getAssignedTo().getUserId(), user.getUserId())) {
            throw new ForbiddenException("This Task is not assigned to this User");
        }
        if (foundTask.getTaskStatus() == TaskStatus.DONE) {
            throw new BadRequestException("This Task is already Completed");
        }
        if (foundTask.getTaskStatus() == TaskStatus.CANCELLED) {
            throw new BadRequestException("This Task is already Cancelled");
        }

        foundTask.setTaskStatus(TaskStatus.DONE);
        foundTask.setUpdatedAt(LocalDateTime.now());
        tasksRepository.save(foundTask);
        AuditLogs logs = AuditLogs.builder().
                action("Updated Task Status as Completed")
                .performedBy("User")
                .targetEntity("User :" + user.getUserName() + " Task :" + taskName)
                .timestamp(LocalDateTime.now()).build();
        auditLogsRepository.save(logs);

    }


}
