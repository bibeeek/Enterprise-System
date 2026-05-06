package com.EnterpriseSystem.demo.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Departments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long departmentId;

    @Column(length = 200,nullable = false,unique = true)
    private String departmentName;

    @Column(length = 500,nullable = false)
    private String departmentDescription;

    private Boolean isActive;

    @OneToMany(mappedBy = "departments")
    private List<Users> users;



    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;





}
