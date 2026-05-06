package com.EnterpriseSystem.demo.Exceptions.CustomExceptions;

public class AccountLockedException extends RuntimeException {
  public AccountLockedException(String message) {
    super(message);
  }
}
