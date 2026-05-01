package com.EnterpriseSystem.demo.Exceptions.CustomExceptions;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
