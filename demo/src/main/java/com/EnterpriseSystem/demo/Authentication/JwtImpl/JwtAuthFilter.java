package com.EnterpriseSystem.demo.Authentication.JwtImpl;

import com.EnterpriseSystem.demo.Authentication.CustomUser;
import com.EnterpriseSystem.demo.Authentication.SecurityConfig;
import com.EnterpriseSystem.demo.Entity.Users;
import com.EnterpriseSystem.demo.Repository.UserRepository;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
//for logging

public class JwtAuthFilter extends OncePerRequestFilter {
        private final UserRepository userRepository;
        private final AuthUtils authUtils;



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("incoming request: {}",request.getRequestURI());




        final String requestTokenHeader=request.getHeader("Authorization");
        if (requestTokenHeader==null || !requestTokenHeader.startsWith("Bearer ")){
            filterChain.doFilter(request,response);
            return;
        }


        String token=requestTokenHeader.split("Bearer ")[1];
        //"Bearer ",jdbkjfsbkjfbejsbfjk"

        String email=authUtils.getUserNameFromToken(token);
        if (email!=null && SecurityContextHolder.getContext().getAuthentication()==null){
            Users user = userRepository.findUsersByEmail(email);

            CustomUser userDetails=new CustomUser(user);

            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken=new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        }
        filterChain.doFilter(request,response);


    }




}
