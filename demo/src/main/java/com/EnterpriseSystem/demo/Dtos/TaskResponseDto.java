package com.EnterpriseSystem.demo.Dtos;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskResponseDto {

    private Long taskId;
    private String taskName;
    private Boolean isActive;
    private String taskDescription;
    private String taskStatus;
    private String taskPriority;
    private String taskCategory;
    private String deadline;
    private String estimatedTime;
    private String createdAt;
    private String updatedAt;
    private String createdBy;
    private String departmentName;
    private String assignedTo;




}
