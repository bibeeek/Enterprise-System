package com.EnterpriseSystem.demo.Services;


import com.EnterpriseSystem.demo.Dtos.DepartmentRequestDto;
import com.EnterpriseSystem.demo.Dtos.DepartmentResponseDto;
import com.EnterpriseSystem.demo.Dtos.UserDto.UserResponseDto;
import com.EnterpriseSystem.demo.Entity.AuditLogs;
import com.EnterpriseSystem.demo.Entity.Departments;
import com.EnterpriseSystem.demo.Entity.Tasks;
import com.EnterpriseSystem.demo.Entity.Users;
import com.EnterpriseSystem.demo.Exceptions.CustomExceptions.BadRequestException;
import com.EnterpriseSystem.demo.Repository.AuditLogsRepository;
import com.EnterpriseSystem.demo.Repository.DepartmentsRepository;
import com.EnterpriseSystem.demo.Repository.TasksRepository;
import com.EnterpriseSystem.demo.Repository.UserRepository;
import com.EnterpriseSystem.demo.Utils.Mapper;
import com.EnterpriseSystem.demo.Utils.Roles;
import com.EnterpriseSystem.demo.Utils.TaskStatus;
import com.EnterpriseSystem.demo.Utils.Validations;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class ManagerServices {

    private final DepartmentsRepository departmentsRepository;
    private final UserRepository userRepository;
    private final Mapper mapper;
    private final Validations validations;
    private final TasksRepository tasksRepository;
    private final AuditLogsRepository auditLogsRepository;




    //Admin can add a new department
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

        AuditLogs logs=AuditLogs.builder().
                action("Added New Department")
                        .performedBy("Admin")
                                .targetEntity("Department :"+departmentRequestDto.getDepartmentName())
                                        .timestamp(LocalDateTime.now()).build();
        auditLogsRepository.save(logs);


    }

    //Manager can assign users to departments
    public void assignUsersToDepartment(String userName,String departmentName){


        Departments foundDepartment = departmentsRepository.findDepartmentsByDepartmentNameIgnoreCase(departmentName);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Users manager = userRepository.findUsersByEmail(email);

        if(manager.getDepartments() == null){
            throw new RuntimeException("Manager is not assigned to any Department");
        }
        if (!Objects.equals(manager.getDepartments().getDepartmentId(), foundDepartment.getDepartmentId())){
            throw new RuntimeException("Manager is not from  this Department");
        }


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
        validations.validateRole(foundUser.getRole(), Roles.ROLE_USER,"This User is not a User, Cannot Assign to a Department");

        if (foundUser.getDepartments() != null){
            throw new RuntimeException("User is already assigned to another Department");
        }





        foundUser.setDepartments(foundDepartment);
        userRepository.save(foundUser);
        AuditLogs logs=AuditLogs.builder().
                action("Assigned User to Department")
                        .performedBy("Manager :"+manager.getUserName() )
                                .targetEntity("User :"+userName+" Department :"+foundDepartment.getDepartmentName())
                                        .timestamp(LocalDateTime.now()).build();
        auditLogsRepository.save(logs);

    }

//moved as part of the admin service
    public void enableDepartment(String departmentName){

        Departments existingDepartment = departmentsRepository.findDepartmentsByDepartmentNameIgnoreCase(departmentName);



        //later add logic to check if the manager is active and belongs to the department

        validations.validateDepartment(existingDepartment,"Department Does not Exists");

        existingDepartment.setIsActive(true);
        departmentsRepository.save(existingDepartment);
        AuditLogs logs=AuditLogs.builder().
                action("Enabled Department")
                        .performedBy("Admin")
                                .targetEntity("Department :"+departmentName)
                                        .timestamp(LocalDateTime.now()).build();
        auditLogsRepository.save(logs);
    }

    //moved as part of the admin service
    public void disableDepartment(String departmentName){

        Departments existingDepartment = departmentsRepository.findDepartmentsByDepartmentNameIgnoreCase(departmentName);



        List<Users> activeUsers = userRepository.findAllByIsActiveTrueAndDepartments(existingDepartment);
        if (!activeUsers.isEmpty()) {
            throw new RuntimeException("Cannot Disable this Department as there are active Users");
        }


        validations.validateDepartment(existingDepartment,"Department Does not Exists");

        existingDepartment.setIsActive(false);
        departmentsRepository.save(existingDepartment);
        AuditLogs logs=AuditLogs.builder().
                action("Disabled Department")
                        .performedBy("Admin")
                                .targetEntity("Department :"+departmentName)
                                        .timestamp(LocalDateTime.now()).build();
        auditLogsRepository.save(logs);
    }


    public List<UserResponseDto> listUsersInDepartment(String departmentName){

        Departments departmentDetails = departmentsRepository.findDepartmentsByDepartmentNameIgnoreCase(departmentName);
        validations.validateDepartment(departmentDetails,"Department Does not Exists");
        validations.validateActive(departmentDetails.getIsActive(),"Department is not active");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Users manager = userRepository.findUsersByEmail(email);
        if(manager.getDepartments() == null){
            throw new RuntimeException("Manager is not assigned to any Department");
        }
        if (!Objects.equals(manager.getDepartments().getDepartmentId(), departmentDetails.getDepartmentId())){
            throw new RuntimeException("Manager is not assigned to this Department");
        }

        List<Users> usersInDepartment = userRepository.findAllUsersByDepartments(departmentDetails);
        if (usersInDepartment.isEmpty()){
            throw new RuntimeException("No Users Assigned to this Department");
        }


        return usersInDepartment.stream().map(mapper::dto).toList();
    }



    public void updateTaskStatusAsCancelled(String userName,String taskName){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Users manager = userRepository.findUsersByEmail(email);

        if(manager.getDepartments() == null){
            throw new RuntimeException("Manager is not assigned to any Department");
        }

        Users user = userRepository.findUsersByUserName(userName);
        validations.validateUser(user,"User Does not Exists");
        validations.validateActive(user.getIsActive(),"User is not active");
        validations.validateDepartment(user.getDepartments(),"User is not assigned to any Department");

        Tasks currentTask = tasksRepository.findByTaskName(taskName);
        validations.validateActive(currentTask.getIsActive(),"Task is not active");
        if (!Objects.equals(currentTask.getAssignedTo().getUserId(), user.getUserId())){
            throw new RuntimeException("This Task is not assigned to this User");
        }

        if (!Objects.equals(currentTask.getDepartments().getDepartmentId(), manager.getDepartments().getDepartmentId())){
            throw new RuntimeException("This Task is not from this Department");
        }
        if (currentTask.getTaskStatus() == TaskStatus.DONE){
            throw new RuntimeException("This Task is already Completed");
        }

        currentTask.setTaskStatus(TaskStatus.CANCELLED);
        tasksRepository.save(currentTask);

    }

    @Scheduled(fixedRate = 1000 * 60 * 60 * 2)
    public void updateTaskStatusAsOverDue(){

        List<Tasks> allActiveTasksWithOverdueDate = tasksRepository.findByIsActiveTrueAndDeadlineBeforeAndTaskStatusNot(LocalDateTime.now(),TaskStatus.OVERDUE);

        for (Tasks task : allActiveTasksWithOverdueDate) {
            task.setTaskStatus(TaskStatus.OVERDUE);

        }
        tasksRepository.saveAll(allActiveTasksWithOverdueDate);
    }

    public void reassignTaskToUser(String userName,String taskName,String targetUserName){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Users currentManager = userRepository.findUsersByEmail(email);
        Users currentEmployee = userRepository.findUsersByUserName(userName);
        Tasks currentTask = tasksRepository.findByTaskName(taskName);

        validations.validateUser(currentManager,"Manager Does not Exists");
      //  validations.validateActive(currentManager.getIsActive(),"Manager is not active");
        validations.validateDepartment(currentManager.getDepartments(),"Manager is not assigned to any Department");
        validations.validateRole(currentManager.getRole(),Roles.ROLE_MANAGER,"This User is not a Manager, Cannot Reassign a Task");

        validations.validateUser(currentEmployee,"Employee Does not Exists");
        validations.validateActive(currentEmployee.getIsActive(),"Employee is not active");
        validations.validateDepartment(currentEmployee.getDepartments(),"Employee is not assigned to any Department");
        validations.validateRole(currentEmployee.getRole(),Roles.ROLE_USER,"This User is not a User, Cannot Reassign a Task");

        validations.validateUser(currentTask,"Task Does not Exists");
        validations.validateActive(currentTask.getIsActive(),"Task is not active");
        validations.validateDepartment(currentTask.getDepartments(),"Task is not assigned to any Department");
        validations.validateUser(currentTask.getAssignedTo(),"This Task is not assigned to any User");

        Users targetUser = userRepository.findUsersByUserName(targetUserName);
        validations.validateUser(targetUser,"Target User Does not Exists");
        validations.validateActive(targetUser.getIsActive(),"Target User is not active");
        validations.validateDepartment(targetUser.getDepartments(),"Target User is not assigned to any Department");
        validations.validateRole(targetUser.getRole(),Roles.ROLE_USER,"This User is not a User, Cannot Reassign a Task");




        if (!Objects.equals(currentTask.getDepartments().getDepartmentId(), currentManager.getDepartments().getDepartmentId())){
            throw new RuntimeException("This Task is not assigned to this Department");
        }

        if (!Objects.equals(currentTask.getAssignedTo().getUserId(), currentEmployee.getUserId())){
            throw new RuntimeException("This Task is not assigned to this User");
        }
        if (!Objects.equals(currentManager.getDepartments().getDepartmentId(), targetUser.getDepartments().getDepartmentId())){
           throw new RuntimeException("This user is not from the same Department");
        }



        if (currentTask.getTaskStatus() == TaskStatus.DONE){
            throw new RuntimeException("This Task is already Completed");
        }
        if (currentTask.getTaskStatus() == TaskStatus.CANCELLED){
            throw new RuntimeException("This Task is already Cancelled");
        }

        if (currentTask.getTaskStatus() == TaskStatus.OVERDUE){
            throw new RuntimeException("This Task is already Overdue");
        }

        currentEmployee.getTasks().remove(currentTask);

        currentTask.setAssignedTo(targetUser);
        targetUser.getTasks().add(currentTask);
        tasksRepository.save(currentTask);


    }

    //newly added methods
    // reusable helper — gets the logged in manager
    private Users getLoggedInManager() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userRepository.findUsersByEmail(email);
    }

    public DepartmentResponseDto getManagerDepartment() {
        Users manager = getLoggedInManager();
        if (manager.getDepartments() == null) {
            throw new RuntimeException("You are not assigned to any department");
        }
        return mapper.departmentResponseDto(manager.getDepartments());
    }

    public List<UserResponseDto> listUsersInManagerDepartment() {
        Users manager = getLoggedInManager();
        if (manager.getDepartments() == null) {
            throw new RuntimeException("You are not assigned to any department");
        }
        Departments dept = manager.getDepartments();
        List<Users> users = userRepository.findAllByDepartmentsAndRole(dept,Roles.ROLE_USER);
        return users.stream().map(mapper::dto).toList();
    }

    public List<UserResponseDto> listManagerInDepartment() {

        Users manager = getLoggedInManager();
        if (manager.getDepartments() == null) {
            throw new RuntimeException("You are not assigned to any department");
        }
        Departments dept = manager.getDepartments();
        List<Users> users = userRepository.findAllByDepartmentsAndRole(dept,Roles.ROLE_MANAGER);
        return users.stream().map(mapper::dto).toList();

    }

    public List<UserResponseDto> listUnassignedUsers(){

        List<Users> unassignedUsers = userRepository.findAllByIsActiveTrueAndRoleAndDepartmentsIsNull(Roles.ROLE_USER);
        if (unassignedUsers.isEmpty()) {
            throw new BadRequestException("No Users are unassigned");
        }

        return unassignedUsers.stream().map(mapper::dto).toList();
    }

    public void unassignUser(String userName){

        Users foundUser = userRepository.findUsersByUserName(userName);
        validations.validateUser(foundUser,"User Does not Exists");

        if (!foundUser.getTasks().isEmpty()){
            throw new RuntimeException("User is already assigned to a Task");
        }

        foundUser.setDepartments(null);
        userRepository.save(foundUser);


    }








}
