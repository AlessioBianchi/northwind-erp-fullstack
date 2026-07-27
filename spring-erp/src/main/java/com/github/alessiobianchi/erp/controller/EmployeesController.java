package com.github.alessiobianchi.erp.controller;

import com.github.alessiobianchi.erp.model.Employee;
import com.github.alessiobianchi.erp.service.EmployeesService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeesController {

    private final EmployeesService service;

    public EmployeesController(EmployeesService service) {
        this.service = service;
    }

    @GetMapping
    public List<Employee> findAllByOrderByEmployeeIdDesc() {
        return service.findAllByOrderByEmployeeIdDesc();
    }

    @GetMapping("/paginated")
    public Page<Employee> findAllByOrderByEmployeeIdDesc(
            @RequestParam int pageNumber,
            @RequestParam int pageSize) {
        return service.findAllByOrderByEmployeeIdDesc(pageNumber, pageSize);
    }

    @PostMapping()
    public ResponseEntity<Employee> create(@RequestBody Employee employee) {
        Employee employeeSaved = service.create(employee);
        return ResponseEntity.ok(employeeSaved);
    }

    @PutMapping("/{employeeId}")
    public ResponseEntity<Employee> update(@PathVariable int employeeId, @RequestBody Employee employee) {
        Employee employeeSaved = service.update(employeeId, employee);
        return ResponseEntity.ok(employeeSaved);
    }

    @DeleteMapping("/delete/{employeeId}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable int employeeId) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "User deleted!");

        if(!service.delete(employeeId)) {
            response.put("message", "There are orders connected to this employee. It can't be deleted.");
        }

        return ResponseEntity.ok(response);
    }
}
