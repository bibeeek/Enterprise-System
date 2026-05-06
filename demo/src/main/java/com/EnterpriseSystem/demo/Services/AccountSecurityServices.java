package com.EnterpriseSystem.demo.Services;

import com.EnterpriseSystem.demo.Entity.Users;
import com.EnterpriseSystem.demo.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AccountSecurityServices {

    private final UserRepository userRepository;

    private static final int MAX_LOGIN_ATTEMPTS = 3;
    private static final int LOCK_TIME_IN_MINUTES = 1;

    public void handleFailedLoginAttempt(Users users){
        int attempts=users.getFailedLoginAttempts()+1;
        users.setFailedLoginAttempts(attempts);

        if (attempts>=MAX_LOGIN_ATTEMPTS){
            users.setAccountNonLocked(false);
            users.setLockTime(LocalDateTime.now());
        }

        userRepository.save(users);
    }

    public void handleSuccessfulLoginAttempt(Users users){

        users.setFailedLoginAttempts(0);
        users.setAccountNonLocked(true);
        users.setLockTime(null);
        userRepository.save(users);


    }

    public void checkAndUnlockAccount(Users users){
       if (!users.getAccountNonLocked()){
           if (users.getLockTime()==null){
               return;
           }
           LocalDateTime unlockTime=users.getLockTime().plusMinutes(LOCK_TIME_IN_MINUTES);
           if(unlockTime.isBefore(LocalDateTime.now())){
               users.setAccountNonLocked(true);
               users.setFailedLoginAttempts(0);
               users.setLockTime(null);
               userRepository.save(users);
           }
       }
    }


}
