package com.github.alessiobianchi.erp.controller;

import com.github.alessiobianchi.erp.config.OpenApiConfig;
import com.github.alessiobianchi.erp.dto.EmployeeDTO;
import com.github.alessiobianchi.erp.model.Employee;
import com.github.alessiobianchi.erp.service.EmployeesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Employees", description = "Employees; they are also the users who log in to the application. Passwords are never returned.")
@RestController
@RequestMapping("/api/v1/employees")
public class EmployeesController {

    private final EmployeesService service;

    public EmployeesController(EmployeesService service) {
        this.service = service;
    }

    @Operation(summary = "List employees",
            description = "Returns all employees, newest first." +
                    "\n\nWith `pageNumber` and `pageSize` it returns a page object instead of an array.")
    @ApiResponse(responseCode = "200", description = "Employee list, or a page of employees when paginated")
    @GetMapping
    public List<EmployeeDTO> findAllByOrderByEmployeeIdDesc() {
        return service.findAllByOrderByEmployeeIdDesc();
    }

    @GetMapping(params = {"pageNumber", "pageSize"})
    public Page<EmployeeDTO> findAllByOrderByEmployeeIdDesc(
            @Parameter(description = "0-based page index; send together with pageSize") @RequestParam int pageNumber,
            @Parameter(description = "Page size; send together with pageNumber") @RequestParam int pageSize) {
        return service.findAllByOrderByEmployeeIdDesc(pageNumber, pageSize);
    }

    @Operation(summary = "Create an employee",
            description = "Creates a new employee. The password is required and stored hashed.\n\n" +
                    "- 400 if the password is missing or blank")
    @ApiResponse(responseCode = "201", description = "Employee created")
    @ApiResponse(responseCode = "400", ref = OpenApiConfig.BAD_REQUEST)
    @PostMapping()
    public ResponseEntity<EmployeeDTO> create(@Valid @RequestBody Employee employee) {
        EmployeeDTO employeeSaved = service.create(employee);
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeSaved);
    }

    @Operation(summary = "Update an employee",
            description = "Updates the employee. Send a blank or null password to keep the current one.\n\n" +
                    "- 404 if the employee does not exist")
    @ApiResponse(responseCode = "200", description = "Employee updated")
    @ApiResponse(responseCode = "400", ref = OpenApiConfig.BAD_REQUEST)
    @ApiResponse(responseCode = "404", ref = OpenApiConfig.NOT_FOUND)
    @PutMapping("/{employeeId}")
    public ResponseEntity<EmployeeDTO> update(@PathVariable int employeeId, @Valid @RequestBody Employee employee) {
        EmployeeDTO employeeSaved = service.update(employeeId, employee);
        return ResponseEntity.ok(employeeSaved);
    }

    @Operation(summary = "Delete an employee",
            description = "Deletes the employee.\n\n- 404 if the employee does not exist\n" +
                    "- 409 if the employee has orders")
    @ApiResponse(responseCode = "204", description = "Employee deleted")
    @ApiResponse(responseCode = "404", ref = OpenApiConfig.NOT_FOUND)
    @ApiResponse(responseCode = "409", ref = OpenApiConfig.CONFLICT)
    @DeleteMapping("/{employeeId}")
    public ResponseEntity<Void> delete(@PathVariable int employeeId) {
        service.delete(employeeId);
        return ResponseEntity.noContent().build();
    }
}
