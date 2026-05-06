package com.EnterpriseSystem.demo.Exceptions;


import com.EnterpriseSystem.demo.Exceptions.CustomExceptions.*;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.AuthenticationException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleResourceNotFoundException(ResourceNotFoundException ex){

        ApiResponse<?> response= new ApiResponse<>(ex.getMessage(),404,LocalDateTime.now(),null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);

    }
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiResponse<?>> handleForbiddenException(ForbiddenException ex){
        ApiResponse<?> response= new ApiResponse<>(ex.getMessage(),403, LocalDateTime.now(),null);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<ApiResponse<?>> handleAccountLockedException(AccountLockedException ex){
        ApiResponse<?> response= new ApiResponse<>(ex.getMessage(),423, LocalDateTime.now(),null);
        return ResponseEntity.status(HttpStatus.LOCKED).body(response);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<?>> handleAuthenticationException(AuthenticationException ex){
        ApiResponse<?> response= new ApiResponse<>(ex.getMessage(),401, LocalDateTime.now(),null);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiResponse<?>> handleJwtException(JwtException ex){
        ApiResponse<?> response= new ApiResponse<>(ex.getMessage(),401, LocalDateTime.now(),null);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<?>> handleAccessDeniedException(AccessDeniedException ex){
        ApiResponse<?> response= new ApiResponse<>(ex.getMessage(),403, LocalDateTime.now(),null);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<?>> handleUnauthorizedException(UnauthorizedException ex){
        ApiResponse<?> response= new ApiResponse<>(ex.getMessage(),401, LocalDateTime.now(),null);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);

    }
    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ApiResponse<?>> handleMalformedJwtException(MalformedJwtException ex){
        ApiResponse<?> response= new ApiResponse<>(ex.getMessage(),401, LocalDateTime.now(),null);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ApiResponse<?>> handleExpiredJwtException(ExpiredJwtException ex){
        ApiResponse<?> response= new ApiResponse<>(ex.getMessage(),401, LocalDateTime.now(),null);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
    //this is for validation exceptions in controller
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex){

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        ApiResponse<?> response= new ApiResponse<>("Validation Failed",400, LocalDateTime.now(),errors);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleAllExceptions(Exception ex){
        ApiResponse<?> response= new ApiResponse<>(ex.getMessage(),500, LocalDateTime.now(),null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

}
