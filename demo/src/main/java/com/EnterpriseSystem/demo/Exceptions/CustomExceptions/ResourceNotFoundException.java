package com.EnterpriseSystem.demo.Exceptions.CustomExceptions;

public class ResourceNotFoundException extends RuntimeException {
  public ResourceNotFoundException(String message) {
    super(message);
  }
}
