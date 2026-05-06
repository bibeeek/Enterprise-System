package com.EnterpriseSystem.demo.Dtos;


import com.EnterpriseSystem.demo.Utils.TaskCategory;
import com.EnterpriseSystem.demo.Utils.TaskPriority;
import com.EnterpriseSystem.demo.Utils.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TaskRequestDto {

    @NotBlank(message = "Task Name is required")
    private String taskName;
    @NotBlank(message = "Task Description is required")
    private String taskDescription;
    private TaskStatus taskStatus;
    private TaskPriority taskPriority;
    private TaskCategory taskCategory;

    private LocalDateTime deadline;

    @NotNull(message = "Estimated time is required")
    private Integer estimatedTimeInHrs;




}
