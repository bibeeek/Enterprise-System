package com.EnterpriseSystem.demo.Exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class ApiResponse<T> {

    private String message;
    private int httpStatus;
    private LocalDateTime timestamp;
    private T data;

}
