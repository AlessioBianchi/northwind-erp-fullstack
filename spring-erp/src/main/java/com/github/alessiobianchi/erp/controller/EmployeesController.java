package com.github.alessiobianchi.erp.controller;

import com.github.alessiobianchi.erp.dto.EmployeeDTO;
import com.github.alessiobianchi.erp.model.Employee;
import com.github.alessiobianchi.erp.service.EmployeesService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeesController {

    private final EmployeesService service;

    public EmployeesController(EmployeesService service) {
        this.service = service;
    }

    @GetMapping
    public List<EmployeeDTO> findAllByOrderByEmployeeIdDesc() {
        return service.findAllByOrderByEmployeeIdDesc();
    }

    @GetMapping(params = {"pageNumber", "pageSize"})
    public Page<EmployeeDTO> findAllByOrderByEmployeeIdDesc(
            @RequestParam int pageNumber,
            @RequestParam int pageSize) {
        return service.findAllByOrderByEmployeeIdDesc(pageNumber, pageSize);
    }

    @PostMapping()
    public ResponseEntity<EmployeeDTO> create(@Valid @RequestBody Employee employee) {
        EmployeeDTO employeeSaved = service.create(employee);
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeSaved);
    }

    @PutMapping("/{employeeId}")
    public ResponseEntity<EmployeeDTO> update(@PathVariable int employeeId, @Valid @RequestBody Employee employee) {
        EmployeeDTO employeeSaved = service.update(employeeId, employee);
        return ResponseEntity.ok(employeeSaved);
    }

    @DeleteMapping("/{employeeId}")
    public ResponseEntity<Void> delete(@PathVariable int employeeId) {
        service.delete(employeeId);
        return ResponseEntity.noContent().build();
    }
}
