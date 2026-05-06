package com.EnterpriseSystem.demo.Services;

import com.EnterpriseSystem.demo.Dtos.TaskRequestDto;
import com.EnterpriseSystem.demo.Dtos.TaskResponseDto;
import com.EnterpriseSystem.demo.Entity.AuditLogs;
import com.EnterpriseSystem.demo.Entity.Tasks;
import com.EnterpriseSystem.demo.Entity.Users;
import com.EnterpriseSystem.demo.Exceptions.CustomExceptions.BadRequestException;
import com.EnterpriseSystem.demo.Exceptions.CustomExceptions.ForbiddenException;
import com.EnterpriseSystem.demo.Exceptions.CustomExceptions.ResourceNotFoundException;
import com.EnterpriseSystem.demo.Repository.AuditLogsRepository;
import com.EnterpriseSystem.demo.Repository.TasksRepository;
import com.EnterpriseSystem.demo.Repository.UserRepository;
import com.EnterpriseSystem.demo.Utils.Mapper;
import com.EnterpriseSystem.demo.Utils.Roles;
import com.EnterpriseSystem.demo.Utils.TaskStatus;
import com.EnterpriseSystem.demo.Utils.Validations;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.config.Task;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskServices {

    private final TasksRepository tasksRepository;
    private final Mapper mapper;
    private final UserRepository userRepository;
    private final Validations validations;
    private final AuditLogsRepository auditLogsRepository;



    private Users getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        Users user = userRepository.findUsersByEmail(email);
        if (user == null) {
            throw new ResourceNotFoundException("Authenticated user not found");
        }
        return user;
    }

    //Only Manager can add a new task only for their assigned department
    public  void addANewTask(TaskRequestDto taskRequestDto){

        Users manager = getCurrentUser();

        validations.validateUser(manager,"Manager Does not Exists");
        validations.validateRole(manager.getRole(),Roles.ROLE_MANAGER,"The Person is not a Manager");
        validations.validateDepartment(manager.getDepartments(),"Manager is not from this Department");

        boolean existingTask = tasksRepository.existsByTaskName((taskRequestDto.getTaskName()));
        if (existingTask){
            throw new BadRequestException("Task With Name "+ taskRequestDto.getTaskName()+ " " +"Exists");
        }

        if (taskRequestDto.getEstimatedTimeInHrs() < 0){
            throw new BadRequestException("Estimated Time cannot be 0 or less");
        }
        if (taskRequestDto.getDeadline().isBefore(LocalDateTime.now()) ||
                taskRequestDto.getDeadline().isEqual(LocalDateTime.now())){
            throw new BadRequestException("Deadline cannot be in the past");
        }

        Tasks task=Tasks.builder()
                .taskName(taskRequestDto.getTaskName())
                .taskDescription(taskRequestDto.getTaskDescription())
                .taskCategory(taskRequestDto.getTaskCategory())
                .taskPriority(taskRequestDto.getTaskPriority())
                .taskStatus(TaskStatus.OPEN)
                .deadline(taskRequestDto.getDeadline())
                .estimatedTimeInHrs(taskRequestDto.getEstimatedTimeInHrs())
                .createdAt(LocalDateTime.now())
                .isActive(true)
                .departments(manager.getDepartments())
                .createdBy(manager)
                .build();
        tasksRepository.save(task);

        AuditLogs logs=AuditLogs.builder().
                action("Added New Task")
                        .performedBy("Manager: "+manager.getUserName())
                                .targetEntity("Task :"+taskRequestDto.getTaskName())
                                        .timestamp(LocalDateTime.now()).build();
        auditLogsRepository.save(logs);

    }

    //Only Managers can view all tasks for their assigned departments
    public List<TaskResponseDto> viewAllTasks(){

        Users manager = getCurrentUser();
        validations.validateUser(manager,"Cannot find Manager");
        validations.validateDepartment(manager.getDepartments(),"Manager is not assigned to any Department");


        List<Tasks> userTasks = tasksRepository.findAllByDepartmentsAndIsActiveTrue(manager.getDepartments());
        if (userTasks.isEmpty()){
            throw new ResourceNotFoundException("No active tasks found for your department");

        }

       return userTasks.stream().map(mapper::taskResponseDto).toList();

    }


    public void enableTask(String taskName){

        Tasks task = tasksRepository.findByTaskName(taskName);

        //later add logic to check if the manager updating the task is updating the task for their assigned department

        Users manager = getCurrentUser();

        validations.validateUser(manager,"Cannot find Manager");
        validations.validateDepartment(manager.getDepartments(),"Manager is not assigned to any Department");

        if (task == null){
            throw new ResourceNotFoundException("Task not found: " + taskName);
        }

        if (!Objects.equals(manager.getDepartments().getDepartmentId(), task.getDepartments().getDepartmentId())){
            throw new ForbiddenException("You are not allowed to modify tasks from another department");
        }

        task.setIsActive(true);
        tasksRepository.save(task);
        AuditLogs logs=AuditLogs.builder().
                action("Enabled Task")
                        .performedBy("Manager" + manager.getUserName())
                                .targetEntity("Task :"+taskName)
                                        .timestamp(LocalDateTime.now()).build();
        auditLogsRepository.save(logs);

    }
    public void disableTask(String taskName){
        Tasks task = tasksRepository.findByTaskName(taskName);

        Users manager = getCurrentUser();

        validations.validateUser(manager,"Cannot find Manager");
        validations.validateDepartment(manager.getDepartments(),"Manager is not assigned to any Department");


        if (task == null){
            throw new ResourceNotFoundException("Task not found: " + taskName);

        }
        if (!Objects.equals(manager.getDepartments().getDepartmentId(), task.getDepartments().getDepartmentId())){
            throw new ForbiddenException("You are not allowed to modify this task");
        }


        task.setIsActive(false);
        tasksRepository.save(task);
        AuditLogs logs=AuditLogs.builder().
                action("Disabled Task")
                        .performedBy("Manager" + manager.getUserName())
                                .targetEntity("Task :"+taskName)
                                        .timestamp(LocalDateTime.now()).build();
        auditLogsRepository.save(logs);
    }


    //Only Managers can assign a task to a user for their assigned departments
    public void assignTaskToUser(String EmployeeUserName,String taskName) {

        Users foundManager = getCurrentUser();

        Users user = userRepository.findUsersByUserName(EmployeeUserName);
        Tasks foundTask = tasksRepository.findByTaskName(taskName);

        validations.validateUser(user, "User Does not Exist");
        validations.validateActive(user.getIsActive(), "User is not active");
        validations.validateDepartment(user.getDepartments(), "User is not assigned to any Department");
        validations.validateRole(user.getRole(),Roles.ROLE_USER,"This User does not have user Role, Cannot Assign a Task");


        validations.validateUser(foundManager, "Cannot find Manager");
        validations.validateDepartment(foundManager.getDepartments(), "Manager is not assigned to any Department");


        if (!Objects.equals(foundManager.getDepartments().getDepartmentId(), user.getDepartments().getDepartmentId())){
            throw new ForbiddenException("User does not belong to your department");
        }

        if (foundTask == null){
            throw new ResourceNotFoundException("Task not found: " + taskName);
        }
        validations.validateActive(foundTask.getIsActive(), "Task is not active");
        validations.validateDepartment(foundTask.getDepartments(), "Task is not assigned to any Department");

        if(!Objects.equals(foundTask.getDepartments().getDepartmentId(), foundManager.getDepartments().getDepartmentId())){
            throw new ForbiddenException("Task does not belong to your department");
        }
        if (foundTask.getAssignedTo() != null){
            throw new BadRequestException("Task is already assigned to another user");
        }
        if (foundTask.getTaskStatus() == TaskStatus.DONE || foundTask.getTaskStatus() == TaskStatus.CANCELLED){
            throw new BadRequestException("Cannot assign completed or cancelled task");
        }
        if (foundTask.getTaskStatus()==TaskStatus.OVERDUE|| foundTask.getDeadline().isBefore(LocalDateTime.now())){
            throw new BadRequestException("Cannot assign overdue task");
        }

        if (countTaskPerUser(EmployeeUserName)>=3){
            throw new BadRequestException("User has reached maximum task limit (3 tasks)");
        }

        foundTask.setAssignedTo(user);
        foundTask.setTaskStatus(TaskStatus.IN_PROGRESS);
        user.getTasks().add(foundTask);


        userRepository.save(user);
        tasksRepository.save(foundTask);
        AuditLogs logs=AuditLogs.builder().
                action("Assigned Task to User")
                        .performedBy("Manager")
                                .targetEntity("Task :"+taskName+" User :"+EmployeeUserName)
                                        .timestamp(LocalDateTime.now()).build();
        auditLogsRepository.save(logs);

    }

    public long countTaskPerUser(String userName){

        Users retrievedUser = userRepository.findUsersByUserName(userName);


        long userTaskCount = tasksRepository.countTasksByAssignedTo(retrievedUser);
        System.out.println("User : "+userName+" has "+userTaskCount+" tasks assigned to him");

        return userTaskCount;

    }










}
