package com.github.alessiobianchi.erp.controller;

import com.github.alessiobianchi.erp.config.OpenApiConfig;
import com.github.alessiobianchi.erp.dto.ChangePasswordDTO;
import com.github.alessiobianchi.erp.dto.EmployeeDTO;
import com.github.alessiobianchi.erp.model.Employee;
import com.github.alessiobianchi.erp.service.EmployeesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

import static com.github.alessiobianchi.erp.controller.Paging.MAX_PAGE_SIZE;

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
                    "\n\nWith `pageNumber` and `pageSize` it returns a page object instead of an array; " +
                    "add `search` to filter it by the first name, last name or title.")
    @ApiResponse(responseCode = "200", description = "Employee list, or a page of employees when paginated")
    @ApiResponse(responseCode = "400", ref = OpenApiConfig.BAD_REQUEST)
    @GetMapping
    public List<EmployeeDTO> findAllByOrderByEmployeeIdDesc() {
        return service.findAllByOrderByEmployeeIdDesc();
    }

    @GetMapping(params = {"pageNumber", "pageSize"})
    public Page<EmployeeDTO> findAllByOrderByEmployeeIdDesc(
            @Parameter(description = "0-based page index (0 or greater); send together with pageSize")
            @RequestParam @Min(value = 0, message = "must be 0 or greater") int pageNumber,
            @Parameter(description = "Page size (1 to " + MAX_PAGE_SIZE + "); send together with pageNumber")
            @RequestParam @Min(value = 1, message = "must be at least 1")
            @Max(value = MAX_PAGE_SIZE, message = "must be at most {value}") int pageSize,
            @Parameter(description = "Case-insensitive text to look for in the first name, last name or title, across all pages; missing or blank returns every row")
            @RequestParam(required = false) String search) {
        return service.findAllByOrderByEmployeeIdDesc(pageNumber, pageSize, search);
    }

    @Operation(summary = "Create an employee",
            description = "Managers only. Creates a new employee. The password is required and stored hashed." +
                    "\n\nPassword rules: at least 8 characters, at most 72 bytes.\n\n" +
                    "- 400 if the password is missing or breaks the rules")
    @ApiResponse(responseCode = "201", description = "Employee created")
    @ApiResponse(responseCode = "400", ref = OpenApiConfig.BAD_REQUEST)
    @ApiResponse(responseCode = "403", ref = OpenApiConfig.MANAGER_ONLY)
    @PostMapping()
    public ResponseEntity<EmployeeDTO> create(@Valid @RequestBody Employee employee) {
        EmployeeDTO employeeSaved = service.create(employee);
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeSaved);
    }

    @Operation(summary = "Update an employee",
            description = "Managers only. Updates the employee. Send a blank or null password to keep the current one; " +
                    "a new password resets it without asking for the old one." +
                    "\n\nPassword rules: at least 8 characters, at most 72 bytes.\n\n" +
                    "- 400 if the new password breaks the rules\n" +
                    "- 404 if the employee does not exist")
    @ApiResponse(responseCode = "200", description = "Employee updated")
    @ApiResponse(responseCode = "400", ref = OpenApiConfig.BAD_REQUEST)
    @ApiResponse(responseCode = "403", ref = OpenApiConfig.MANAGER_ONLY)
    @ApiResponse(responseCode = "404", ref = OpenApiConfig.NOT_FOUND)
    @PutMapping("/{employeeId}")
    public ResponseEntity<EmployeeDTO> update(@PathVariable int employeeId, @Valid @RequestBody Employee employee) {
        EmployeeDTO employeeSaved = service.update(employeeId, employee);
        return ResponseEntity.ok(employeeSaved);
    }

    @Operation(summary = "Delete an employee",
            description = "Managers only. Deletes the employee.\n\n- 404 if the employee does not exist\n" +
                    "- 409 if the employee has orders")
    @ApiResponse(responseCode = "204", description = "Employee deleted")
    @ApiResponse(responseCode = "403", ref = OpenApiConfig.MANAGER_ONLY)
    @ApiResponse(responseCode = "404", ref = OpenApiConfig.NOT_FOUND)
    @ApiResponse(responseCode = "409", ref = OpenApiConfig.CONFLICT)
    @DeleteMapping("/{employeeId}")
    public ResponseEntity<Void> delete(@PathVariable int employeeId) {
        service.delete(employeeId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Change my password",
            description = "Changes the password of the logged-in employee. Open to every employee." +
                    "\n\nPassword rules: at least 8 characters, at most 72 bytes.\n\n" +
                    "- 400 if the current password is wrong or the new one breaks the rules")
    @ApiResponse(responseCode = "204", description = "Password changed")
    @ApiResponse(responseCode = "400", ref = OpenApiConfig.BAD_REQUEST)
    @PutMapping("/me/password")
    public ResponseEntity<Void> changeOwnPassword(@Valid @RequestBody ChangePasswordDTO body,
                                                  @Parameter(hidden = true) Principal principal) {
        service.changeOwnPassword(principal.getName(), body.currentPassword(), body.newPassword());
        return ResponseEntity.noContent().build();
    }
}
