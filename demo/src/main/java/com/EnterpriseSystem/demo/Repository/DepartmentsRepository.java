package com.EnterpriseSystem.demo.Repository;

import com.EnterpriseSystem.demo.Entity.Departments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentsRepository extends JpaRepository<Departments, Long> {
    Boolean existsByDepartmentName(String departmentName);

   public Departments findDepartmentsByDepartmentNameIgnoreCase(String departmentName);



}