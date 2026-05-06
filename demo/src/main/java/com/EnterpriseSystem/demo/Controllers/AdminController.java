package com.EnterpriseSystem.demo.Controllers;


import com.EnterpriseSystem.demo.Dtos.AdminResponseDto;
import com.EnterpriseSystem.demo.Dtos.DepartmentRequestDto;
import com.EnterpriseSystem.demo.Dtos.DepartmentResponseDto;
import com.EnterpriseSystem.demo.Dtos.UserDto.UserRegistrationDto;
import com.EnterpriseSystem.demo.Entity.AuditLogs;
import com.EnterpriseSystem.demo.Exceptions.ApiResponse;
import com.EnterpriseSystem.demo.Services.AdminServices;
import com.EnterpriseSystem.demo.Services.AuditLogsServices;
import com.EnterpriseSystem.demo.Services.ManagerServices;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.apache.catalina.Manager;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminServices adminServices;
    private final ManagerServices managerServices;
    private final AuditLogsServices auditLogsServices;

    @PutMapping("/enableUser/{username}")
    public ResponseEntity<ApiResponse<?>> enableUser(@PathVariable String username) {

        adminServices.enableUser(username);
        ApiResponse<String> response = new ApiResponse<>("User Enabled Successfully", 200, LocalDateTime.now(), null);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/disableUser/{username}")
    public ResponseEntity<ApiResponse<?>> disableUser(@PathVariable String username) {

        adminServices.disableUser(username);
        ApiResponse<String> response = new ApiResponse<>("User Disabled Successfully", 200, LocalDateTime.now(), null);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/enableManager/{username}")
    public ResponseEntity<ApiResponse<?>> enableManager(@PathVariable String username) {

        adminServices.enableManager(username);
        ApiResponse<String> response = new ApiResponse<>("Manager Enabled Successfully", 200, LocalDateTime.now(), null);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/disableManager/{username}")
    public ResponseEntity<ApiResponse<?>> disableManager(@PathVariable String username) {

        adminServices.disableManager(username);
        ApiResponse<String> response = new ApiResponse<>("Manager Disabled Successfully", 200, LocalDateTime.now(), null);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/addAdmin")
    public ResponseEntity<ApiResponse<?>> addNewAdmin(@RequestBody @Valid UserRegistrationDto userRegistrationDto) {

        adminServices.addNewAdmin(userRegistrationDto);
        ApiResponse<String> response = new ApiResponse<>("Admin Added Successfully", 200, LocalDateTime.now(), null);
        return ResponseEntity.ok(response);

    }

    @PostMapping("/addManager")
    public ResponseEntity<ApiResponse<?>> addNewManager(@RequestBody @Valid UserRegistrationDto userRegistrationDto) {

        adminServices.addManager(userRegistrationDto);
        ApiResponse<String> response = new ApiResponse<>("Manager Added Successfully", 200, LocalDateTime.now(), null);
        return ResponseEntity.ok(response);

    }


    @GetMapping("/viewAllUsers")
    public ResponseEntity<ApiResponse<List<AdminResponseDto>>> getAllUsers(@RequestParam(required = false,defaultValue = "0") int page,
                                                                           @RequestParam(required = false,defaultValue = "10") int size
                                                                           ) {

        List<AdminResponseDto> allActiveUsers = adminServices.getAllActiveUsers(page, size);
        ApiResponse<List<AdminResponseDto>> response = new ApiResponse<>("All Active Users", 200, LocalDateTime.now(), allActiveUsers);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/addDepartment")
    public ResponseEntity<ApiResponse<?>> addDepartment(@RequestBody @Valid DepartmentRequestDto departmentRequestDto) {

        managerServices.addDepartment(departmentRequestDto);
        ApiResponse<?> response = new ApiResponse<>("Department Added Successfully", 200, LocalDateTime.now(), null);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/assignManagersToDepartment/{managerUserName}/{departmentName}")
    public ResponseEntity<ApiResponse<?>> assignManagersToDepartment(@PathVariable String managerUserName, @PathVariable String departmentName) {

        adminServices.assignManagersToDepartment(managerUserName, departmentName);
        ApiResponse<?> response = new ApiResponse<>("Managers Assigned to Department Successfully", 200, LocalDateTime.now(), null);
        return ResponseEntity.ok(response);

    }

    @GetMapping("/getAllActiveManagers")
    public ResponseEntity<ApiResponse<List<AdminResponseDto>>> getAllActiveManagers(@RequestParam(required = false,defaultValue = "0") int page,
                                                                                    @RequestParam(required = false,defaultValue = "10") int size) {
        List<AdminResponseDto> allActiveManagers = adminServices.getAllActiveManagers(page, size);
        ApiResponse<List<AdminResponseDto>> response = new ApiResponse<>("All Active Managers", 200, LocalDateTime.now(), allActiveManagers);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getAllActiveAdmins")
    public ResponseEntity<ApiResponse<List<AdminResponseDto>>> getAllActiveAdmins(@RequestParam(required = false,defaultValue = "0") int page,
                                                                                  @RequestParam(required = false,defaultValue = "10") int size) {
        List<AdminResponseDto> allActiveAdmins = adminServices.getAllActiveAdmins(page, size);
        ApiResponse<List<AdminResponseDto>> response = new ApiResponse<>("All Active Admins", 200, LocalDateTime.now(), allActiveAdmins);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/viewAllDepartments")
    public ResponseEntity<ApiResponse<List<DepartmentResponseDto>>> viewAllDepartments() {

        List<DepartmentResponseDto> departmentList = adminServices.viewAllDepartments();
        ApiResponse<List<DepartmentResponseDto>> response = new ApiResponse<>("All Departments", 200, LocalDateTime.now(), departmentList);
        return ResponseEntity.ok(response);

    }

    @GetMapping("/viewAllAuditLogs")
    public ResponseEntity<ApiResponse<List<AuditLogs>>> viewAllAuditLogs(@RequestParam(required = false, defaultValue = "0") int page, @RequestParam(required = false, defaultValue = "10") int size,
                                                                         @RequestParam(required = false, defaultValue = "DESC") String order, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                                                         @RequestParam(required = false) String sortBy
    ) {

        List<AuditLogs> auditLogs = auditLogsServices.viewAuditLogs(page, size, startDate, endDate, order,sortBy);
        ApiResponse<List<AuditLogs>> response = new ApiResponse<>("All Audit Logs", 200, LocalDateTime.now(), auditLogs);
        return ResponseEntity.ok(response);
    }


}
