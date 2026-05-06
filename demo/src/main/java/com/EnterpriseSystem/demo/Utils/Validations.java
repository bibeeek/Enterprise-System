package com.EnterpriseSystem.demo.Utils;

import com.EnterpriseSystem.demo.Exceptions.CustomExceptions.AccountLockedException;
import com.EnterpriseSystem.demo.Exceptions.CustomExceptions.BadRequestException;
import com.EnterpriseSystem.demo.Exceptions.CustomExceptions.ForbiddenException;
import com.EnterpriseSystem.demo.Exceptions.CustomExceptions.ResourceNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class Validations {


    public  <T> T getOrThrow(Optional<T> optional, String message) {
        return optional.orElseThrow(() -> new RuntimeException(message));
    }

    public void validateActive(Boolean isActive, String message) {
        if (Boolean.FALSE.equals(isActive)) {
            throw new AccountLockedException(message);
        }
    }
    public void validateDepartment(Object department, String message) {
        if (department == null) {
            throw new ForbiddenException(message);
        }
    }

    public void validateUser(Object user, String message) {
        if (user == null) {
            throw new ResourceNotFoundException(message);
        }
    }

    public void validateRole(Roles role, Roles expected, String message) {
        if (role != expected) {
            throw new BadRequestException(message);
        }
    }
}
