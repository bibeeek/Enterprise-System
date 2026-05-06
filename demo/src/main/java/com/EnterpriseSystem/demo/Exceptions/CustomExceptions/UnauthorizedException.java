package com.EnterpriseSystem.demo.Exceptions.CustomExceptions;

public class UnauthorizedException extends RuntimeException {
  public UnauthorizedException(String message) {
    super(message);
  }
}
