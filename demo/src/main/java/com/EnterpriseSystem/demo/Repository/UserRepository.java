package com.EnterpriseSystem.demo.Repository;

import com.EnterpriseSystem.demo.Entity.Departments;
import com.EnterpriseSystem.demo.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users,Long> {


    Boolean existsByEmail(String email);

    public  Users findUsersByEmail(String email);

    public Users findUsersByUserName(String userName);

    public Users findUsersByUserIdAndUserNameIgnoreCase(Long userId, String userName);

    public List<Users> findAllByIsActiveTrue();

    List<Users> findAllUsersByDepartments(Departments departments);

    Boolean existsByUserIdAndDepartments(Long userId, Departments departments);

    boolean existsByUserName(String userName);
}
