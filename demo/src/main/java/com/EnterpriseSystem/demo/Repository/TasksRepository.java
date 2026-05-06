package com.EnterpriseSystem.demo.Repository;

import com.EnterpriseSystem.demo.Entity.Departments;
import com.EnterpriseSystem.demo.Entity.Tasks;
import com.EnterpriseSystem.demo.Entity.Users;
import com.EnterpriseSystem.demo.Utils.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TasksRepository extends JpaRepository<Tasks, Long> {

    List<Tasks> findAllByCreatedByAndDepartmentsAndIsActiveTrue(Users createdBy, Departments departments);

    boolean existsByTaskName(String taskName);

    Tasks findByTaskName(String taskName);

    long countTasksByAssignedTo(Users assignedTo);

    List<Tasks> findAllByAssignedTo(Users assignedTo);



    List<Tasks> findByIsActiveTrueAndDeadlineBeforeAndTaskStatusNot(LocalDateTime now, TaskStatus taskStatus);

    List<Tasks> findAllByDepartmentsAndIsActiveTrue(Departments departments);
}