package com.EnterpriseSystem.demo.Repository;

import com.EnterpriseSystem.demo.Entity.Departments;
import com.EnterpriseSystem.demo.Entity.Users;
import com.EnterpriseSystem.demo.Utils.Roles;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UserRepository extends JpaRepository<Users,Long> {


    Boolean existsByEmail(String email);

    public  Users findUsersByEmail(String email);

    public Users findUsersByUserName(String userName);



    public List<Users> findAllByIsActiveTrue();

    List<Users> findAllUsersByDepartments(Departments departments);


    boolean existsByUserName(String userName);

    Boolean existsByUserNameAndDepartments(String userName, Departments departments);

    List<Users> findAllByIsActiveTrueAndRole(Roles roles, Pageable pageable);
}
