package com.EnterpriseSystem.demo.Dtos;


import com.EnterpriseSystem.demo.Entity.Users;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter

public class DepartmentResponseDto {

    private String departmentName;

    private String departmentDescription;

    private String createdAt;

    private Boolean isActive;

    private List<String> managersUserNames;





}
