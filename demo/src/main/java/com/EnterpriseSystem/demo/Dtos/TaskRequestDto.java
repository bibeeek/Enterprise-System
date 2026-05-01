package com.EnterpriseSystem.demo.Dtos;


import com.EnterpriseSystem.demo.Utils.TaskCategory;
import com.EnterpriseSystem.demo.Utils.TaskPriority;
import com.EnterpriseSystem.demo.Utils.TaskStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TaskRequestDto {

    private String taskName;
    private String taskDescription;
    private TaskStatus taskStatus;
    private TaskPriority taskPriority;
    private TaskCategory taskCategory;
    private LocalDateTime deadline;
    private Integer estimatedTime;




}
