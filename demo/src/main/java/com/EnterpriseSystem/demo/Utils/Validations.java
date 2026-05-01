package com.EnterpriseSystem.demo.Utils;

import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class Validations {


    public  <T> T getOrThrow(Optional<T> optional, String message) {
        return optional.orElseThrow(() -> new RuntimeException(message));
    }

    public void validateActive(Boolean isActive, String message) {
        if (Boolean.FALSE.equals(isActive)) {
            throw new RuntimeException(message);
        }
    }
    public void validateDepartment(Object department, String message) {
        if (department == null) {
            throw new RuntimeException(message);
        }
    }

    public void validateRole(Roles role, Roles expected, String message) {
        if (role != expected) {
            throw new RuntimeException(message);
        }
    }
}
