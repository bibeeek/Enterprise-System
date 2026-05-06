package com.EnterpriseSystem.demo.Dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DepartmentRequestDto {

    @NotBlank(message = "Department Name is required")
    private String departmentName;
    @NotBlank(message = "Department Description is required")
    private String departmentDescription;

}
