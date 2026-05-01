package com.EnterpriseSystem.demo.Services;

import com.EnterpriseSystem.demo.Dtos.UserRequestDto;
import com.EnterpriseSystem.demo.Dtos.UserResponseDto;
import com.EnterpriseSystem.demo.Entity.Users;
import com.EnterpriseSystem.demo.Exceptions.CustomExceptions.BadRequestException;
import com.EnterpriseSystem.demo.Exceptions.CustomExceptions.UserAlreadyExistsException;
import com.EnterpriseSystem.demo.Repository.UserRepository;
import com.EnterpriseSystem.demo.Utils.Mapper;
import com.EnterpriseSystem.demo.Utils.Roles;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class UserServices {

        private final UserRepository userRepository;

        private final Mapper mapper;


        public void registerUser(UserRequestDto userRequestDto){

            boolean userExists = userRepository.existsByUserName((userRequestDto.getUserName()));
            Boolean emailExists = userRepository.existsByEmail(userRequestDto.getEmail());
            if (emailExists){
                throw new UserAlreadyExistsException("User Already Exists with this email");
            }
            if (userExists){
                throw new UserAlreadyExistsException("User Already Exists with this username");
            }
            try{
                Users user= Users.builder()
                        .userName(userRequestDto.getUserName())
                        .passWord(userRequestDto.getPassWord())
                        .email(userRequestDto.getEmail())
                        .role(Roles.ROLE_USER)
                        .createdAt(LocalDateTime.now())
                        .phoneNumber(userRequestDto.getPhoneNumber())
                        .address(userRequestDto.getAddress())
                        .fullName(userRequestDto.getFullName())
                        .isActive(true)
                        .build();

                userRepository.save(user);
            }catch (Exception e){
                throw new RuntimeException(e.getMessage());
            }

        }

        public UserResponseDto login (UserRequestDto userRequestDto){

            Users existingUser = userRepository.findUsersByEmail(userRequestDto.getEmail());
            if (existingUser == null){
                throw new RuntimeException("User Does not Exists");
            }
            if (existingUser.getIsActive() == false){
                throw new RuntimeException("User is not active");
            }
            if (!existingUser.getPassWord().equals(userRequestDto.getPassWord())){
                throw new BadRequestException("Invalid Credentials");
            }
            existingUser.setLastLogin(LocalDateTime.now());
            userRepository.save(existingUser);

            return mapper.dto(existingUser);
        }


        public UserResponseDto getUserDetails(String userName){

            Users user = userRepository.findUsersByUserName(userName);
            if (user == null){
                throw new RuntimeException("User Does not Exists");
            }
            return mapper.dto(user);

        }

        public UserResponseDto updateUserDetails(String userName, UserRequestDto userRequestDto){
            Users foundUser = userRepository.findUsersByUserName(userName);
            if (foundUser == null){
                throw new RuntimeException("User Does not Exists");
            }
            if (foundUser.getIsActive() == false){
                throw new RuntimeException("Cannot Update :: User is not active");
            }

            if (userRequestDto.getUserName() != null ){
                boolean b = userRepository.existsByUserName(userRequestDto.getUserName());
                if (b){
                    throw new RuntimeException("A User Already Exists with this Username");
                }
                foundUser.setUserName(userRequestDto.getUserName());
            }
            if (userRequestDto.getEmail() != null){
                boolean b = userRepository.existsByEmail(userRequestDto.getEmail());
                if (b){
                    throw new RuntimeException("A User Already Exists with this Email");
                }
                foundUser.setEmail(userRequestDto.getEmail());
            }
            if (userRequestDto.getPassWord() != null){
                foundUser.setPassWord(userRequestDto.getPassWord());
            }
            if (userRequestDto.getAddress() != null){
                foundUser.setAddress(userRequestDto.getAddress());
            }
            if (userRequestDto.getPhoneNumber()!=null){
                foundUser.setPhoneNumber(userRequestDto.getPhoneNumber());
            }
            if (userRequestDto.getFullName() != null){
                foundUser.setFullName(userRequestDto.getFullName());
            }

            foundUser.setUpdatedAt(LocalDateTime.now());
           userRepository.save(foundUser);
           return mapper.dto(foundUser);
        }

}
