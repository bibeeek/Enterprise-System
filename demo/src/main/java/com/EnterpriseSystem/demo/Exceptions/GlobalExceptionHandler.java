package com.EnterpriseSystem.demo.Exceptions;


import com.EnterpriseSystem.demo.Exceptions.CustomExceptions.BadRequestException;
import com.EnterpriseSystem.demo.Exceptions.CustomExceptions.UserAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler{


    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<?>> handleBadCredentialsException(BadRequestException ex){

        ApiResponse<?> response= new ApiResponse<>(ex.getMessage(),400, LocalDateTime.now(),null);
        return ResponseEntity.badRequest().body(response);

    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<?>> handleUserAlreadyExistsException(UserAlreadyExistsException ex){

        ApiResponse<?> response= new ApiResponse<>(ex.getMessage(),409, LocalDateTime.now(),null);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleAllExceptions(Exception ex){
        ApiResponse<?> response= new ApiResponse<>(ex.getMessage(),500, LocalDateTime.now(),null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

}
