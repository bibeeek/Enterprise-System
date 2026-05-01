package com.EnterpriseSystem.demo.Services;

import com.EnterpriseSystem.demo.Dtos.AdminResponseDto;
import com.EnterpriseSystem.demo.Dtos.UserRequestDto;
import com.EnterpriseSystem.demo.Dtos.UserResponseDto;
import com.EnterpriseSystem.demo.Entity.Users;
import com.EnterpriseSystem.demo.Exceptions.CustomExceptions.UserAlreadyExistsException;
import com.EnterpriseSystem.demo.Repository.UserRepository;
import com.EnterpriseSystem.demo.Utils.Mapper;
import com.EnterpriseSystem.demo.Utils.Roles;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServices {

    private final UserRepository userRepository;
    private final Mapper mapper;


    public void addNewAdmin(UserRequestDto userRequestDto){

        Boolean emailExists = userRepository.existsByEmail(userRequestDto.getEmail());
        if (emailExists){
            throw new UserAlreadyExistsException("User Already Exists with this email");
        }
        try{
            Users user= Users.builder()
                    .userName(userRequestDto.getUserName())
                    .passWord(userRequestDto.getPassWord())
                    .email(userRequestDto.getEmail())
                    .role(Roles.ROLE_ADMIN)
                    .createdAt(LocalDateTime.now())
                    .phoneNumber(userRequestDto.getPhoneNumber())
                    .address(userRequestDto.getAddress())
                    .isActive(true)
                    .build();

            userRepository.save(user);
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }

    }
    public void addManager(UserRequestDto userRequestDto){

        Boolean emailExists = userRepository.existsByEmail(userRequestDto.getEmail());
        if (emailExists){
            throw new UserAlreadyExistsException("Manager Already Exists with this email");
        }
        try{
            Users user= Users.builder()
                    .userName(userRequestDto.getUserName())
                    .passWord(userRequestDto.getPassWord())
                    .email(userRequestDto.getEmail())
                    .phoneNumber(userRequestDto.getPhoneNumber())
                    .address(userRequestDto.getAddress())
                    .role(Roles.ROLE_MANAGER)
                    .createdAt(LocalDateTime.now())
                    .isActive(true)
                    .build();

            userRepository.save(user);
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }

    }


    public void disableUser(String userName,Long userId){
        Users user = userRepository.findUsersByUserIdAndUserNameIgnoreCase(userId,userName);
        user.setIsActive(false);
        userRepository.save(user);
    }
    public void enableUser(String userName,Long userId){
        Users user = userRepository.findUsersByUserIdAndUserNameIgnoreCase(userId,userName );
        user.setIsActive(true);
    }

    public List<AdminResponseDto> getAllActiveUsers(){

        List<Users> activeUserList = userRepository.findAllByIsActiveTrue();

        return activeUserList.stream().map(mapper::adminResponseDto).toList();

    }




}
