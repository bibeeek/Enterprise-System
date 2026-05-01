package com.EnterpriseSystem.demo.Services;

import com.EnterpriseSystem.demo.Dtos.TaskRequestDto;
import com.EnterpriseSystem.demo.Dtos.TaskResponseDto;
import com.EnterpriseSystem.demo.Entity.Departments;
import com.EnterpriseSystem.demo.Entity.Tasks;
import com.EnterpriseSystem.demo.Entity.Users;
import com.EnterpriseSystem.demo.Repository.TasksRepository;
import com.EnterpriseSystem.demo.Repository.UserRepository;
import com.EnterpriseSystem.demo.Utils.Mapper;
import com.EnterpriseSystem.demo.Utils.Roles;
import com.EnterpriseSystem.demo.Utils.TaskStatus;
import com.EnterpriseSystem.demo.Utils.Validations;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.scheduling.config.Task;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskServices {

    private final TasksRepository tasksRepository;
    private final Mapper mapper;
    private final UserRepository userRepository;
    private final Validations validations;


    //Only Manager can add a new task only for their assigned department
    public  void addANewTask(Long managerId, TaskRequestDto taskRequestDto){

        Optional<Users> foundUser = userRepository.findById(managerId);
        if (foundUser.isEmpty()){
            throw new RuntimeException("User Does not Exists");
        }
        if (foundUser.get().getIsActive() == false){
            throw new RuntimeException("User is not active");
        }
        if (foundUser.get().getRole() != Roles.ROLE_MANAGER){
            throw new RuntimeException("User is not a Manager, Cannot Add a Task");
        }
        if (foundUser.get().getDepartments() == null){
            throw new RuntimeException("User is not assigned to any Department");
        }

        boolean existingTask = tasksRepository.existsByTaskName((taskRequestDto.getTaskName()));
        if (existingTask){
            throw new RuntimeException("Task Already Exists");
        }


        Tasks task=Tasks.builder()
                .taskName(taskRequestDto.getTaskName())
                .taskDescription(taskRequestDto.getTaskDescription())
                .taskCategory(taskRequestDto.getTaskCategory())
                .taskPriority(taskRequestDto.getTaskPriority())
                .taskStatus(TaskStatus.OPEN)
                .deadline(taskRequestDto.getDeadline())
                .estimatedTime(taskRequestDto.getEstimatedTime())
                .createdAt(LocalDateTime.now())
                .isActive(true)
                .departments(foundUser.get().getDepartments())
                .createdBy(foundUser.get())
                .build();
        tasksRepository.save(task);

    }

    //Only Managers can view all tasks for their assigned departments
    public List<TaskResponseDto> viewAllTasks(Long managerId){

        Optional<Users> foundUser = userRepository.findById(managerId);
        if (foundUser.isEmpty()){
            throw new RuntimeException("User Does not Exists");
        }
        if (foundUser.get().getIsActive() == false){
            throw new RuntimeException("User is not active");
        }
        if (foundUser.get().getRole() != Roles.ROLE_MANAGER){
            throw new RuntimeException("User is not a Manager, Cannot View Tasks");
        }
        if (foundUser.get().getDepartments() == null){
            throw new RuntimeException("User is not assigned to any Department");
        }

        List<Tasks> userTasks = tasksRepository.findAllByCreatedByAndDepartments(foundUser.get(), foundUser.get().getDepartments());

       return userTasks.stream().map(mapper::taskResponseDto).toList();


    }


    public void enableTask(Long taskId){
        Tasks foundTask = tasksRepository.findById(taskId).orElseThrow(()->new RuntimeException("Task Does not Exists"));
        foundTask.setIsActive(true);
    }
    public void disableTask(Long taskId){
        Tasks foundTask = tasksRepository.findById(taskId).orElseThrow(()->new RuntimeException("Task Does not Exists"));
        foundTask.setIsActive(false);
    }


    //Only Managers can assign a task to a user for their assigned departments
    public void assignTaskToUser(Long managerId,Long userId,Long taskId) {

        Optional<Users> foundManager = userRepository.findById(managerId);
        Optional<Users> foundUser = userRepository.findById(userId);
        Optional<Tasks> foundTask = tasksRepository.findById(taskId);


        Users user = validations.getOrThrow(foundUser, "User Does not Exist");
        validations.validateActive(user.getIsActive(), "User is not active");
        validations.validateDepartment(user.getDepartments(), "User is not assigned to any Department");


        Users manager = validations.getOrThrow(foundManager, "Manager Does not Exist");
        validations.validateActive(manager.getIsActive(), "Manager is not active");
        validations.validateRole(manager.getRole(), Roles.ROLE_MANAGER, "This User is not a Manager, Cannot Assign a Task");
        validations.validateDepartment(manager.getDepartments(), "Manager is not assigned to any Department");

        if (manager.getDepartments() != user.getDepartments()){
            throw new RuntimeException("User is not assigned to the this Department");
        }




        Tasks task = validations.getOrThrow(foundTask, "Task Does not Exist");
        validations.validateActive(task.getIsActive(), "Task is not active");
        validations.validateDepartment(task.getDepartments(), "Task is not assigned to any Department");

        if(task.getDepartments() != manager.getDepartments()){
            throw new RuntimeException("This Task is not assigned to the this Department");
        }
        if (task.getAssignedTo() != null){
            throw new RuntimeException("This Task is already assigned to a User");
        }

        task.setAssignedTo(user);
        user.getTasks().add(task);

        userRepository.save(user);
        tasksRepository.save(task);

    }







}
