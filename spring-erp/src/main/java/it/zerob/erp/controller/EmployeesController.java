package it.zerob.erp.controller;

import it.zerob.erp.model.Employee;
import it.zerob.erp.model.User;
import it.zerob.erp.service.EmployeesService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/v1/employees")
public class EmployeesController {

    private final EmployeesService service;

    public EmployeesController(EmployeesService service) {
        this.service = service;
    }

    @GetMapping
    public String showEmployees(Model model) { return "employees"; }

    @GetMapping("/all")
    @ResponseBody
    public List<Employee> findAllByOrderByEmployeeIdDesc() {
        return service.findAllByOrderByEmployeeIdDesc();
    }

    @GetMapping("/paginated")
    @ResponseBody
    public Page<Employee> findAllByOrderByEmployeeIdDesc(
            @RequestParam int pageNumber,
            @RequestParam int pageSize) {
        return service.findAllByOrderByEmployeeIdDesc(pageNumber, pageSize);
    }

    @PostMapping("/save")
    @ResponseBody
    public ResponseEntity<Employee> save(@RequestBody Employee employee) {
        Employee employeeSaved = service.save(employee);
        return ResponseEntity.ok(employeeSaved);
    }

    @GetMapping("/delete/{employeeId}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long employeeId) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "User deleted!");

        if(!service.delete(employeeId)) {
            response.put("message", "There are orders connected to this employee. It can't be deleted.");
        }

        return ResponseEntity.ok(response);
    }
}
