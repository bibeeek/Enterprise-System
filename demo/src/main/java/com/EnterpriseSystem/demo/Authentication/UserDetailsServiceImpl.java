package com.EnterpriseSystem.demo.Authentication;

import com.EnterpriseSystem.demo.Entity.Users;
import com.EnterpriseSystem.demo.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service

public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private  UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Users user = userRepository.findUsersByEmail(username);
        if (user==null){
            throw new UsernameNotFoundException("User Not Found");
        }

        return new CustomUser(user);

    }
}
