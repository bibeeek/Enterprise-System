package com.EnterpriseSystem.demo.Entity;


import com.EnterpriseSystem.demo.Utils.Roles;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.cglib.core.Local;

import javax.management.relation.Role;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Users {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(length = 100,nullable = false,unique = true)
    private String userName;

    @Column(length = 100,nullable = false)
    private String fullName;

    @Column(length = 100,nullable = false)
    private String passWord;

    @Column(length = 150,unique = true,updatable = false,nullable = false)
    private String email;

    @Column(updatable = false,nullable = false)
    @Enumerated(EnumType.STRING)
    private Roles role;

    @Column(length = 200,nullable = false)
    private String address;

    @Column(length = 10,nullable = false)
    private Long phoneNumber;

    private LocalDateTime lastLogin;

    private Boolean isActive;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @ManyToOne
    private Departments departments;

    @OneToMany(mappedBy = "assignedTo")
    private List<Tasks> tasks;









}
