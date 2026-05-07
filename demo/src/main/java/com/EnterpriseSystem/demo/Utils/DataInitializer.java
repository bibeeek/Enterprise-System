package com.EnterpriseSystem.demo.Utils;

import com.EnterpriseSystem.demo.Entity.Users;
import com.EnterpriseSystem.demo.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        if (userRepository.findUsersByEmail("admin@enterprise.com")==null){

            Users admin = new Users();
            admin.setUserName("admin");
            admin.setPassWord(passwordEncoder.encode("admin1234"));
            admin.setAddress("Nepal");
            admin.setLockTime(null);
            admin.setAccountNonLocked(true);
            admin.setIsActive(true);
            admin.setEmail("admin@enterprise.com");
            admin.setRole(Roles.ROLE_ADMIN);

            admin.setFullName("Admin");
            admin.setPhoneNumber("0123456789");
            admin.setCreatedAt(LocalDateTime.now());
            admin.setFailedLoginAttempts(0);
            userRepository.save(admin);
            System.out.println("Admin Created");

        }


    }
}
