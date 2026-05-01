package com.EnterpriseSystem.demo.Entity;


import com.EnterpriseSystem.demo.Utils.TaskCategory;
import com.EnterpriseSystem.demo.Utils.TaskPriority;
import com.EnterpriseSystem.demo.Utils.TaskStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Tasks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long taskId;

    @ManyToOne
    private Users createdBy;

    @ManyToOne
    private Departments departments;

    @Column(length = 200,nullable = false)
    private String taskName;

    @Column(length = 500,nullable = false)
    private String taskDescription;

    private Boolean isActive;

    @Enumerated(EnumType.STRING)
    private TaskStatus taskStatus;

    @Enumerated(EnumType.STRING)
    private TaskPriority taskPriority;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private TaskCategory taskCategory;

    @Column(nullable = false)
    private Integer estimatedTime;

    @Column(nullable = false)
    private LocalDateTime deadline;

    private LocalDateTime updatedAt;

    @ManyToOne
    private Users assignedTo;

}
