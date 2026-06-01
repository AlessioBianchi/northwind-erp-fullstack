package it.zerob.erp.service;

import it.zerob.erp.dao.OrdersDAO;
import it.zerob.erp.dao.EmployeesDAO;
import it.zerob.erp.model.Employee;
import it.zerob.erp.model.EmployeeBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeesService {

    private final EmployeesDAO employeesDao;
    private final OrdersDAO ordersDao;

    public EmployeesService(EmployeesDAO employeesDao, OrdersDAO ordersDao) {
        this.employeesDao = employeesDao;
        this.ordersDao = ordersDao;
    }

    public Optional<Employee> findByUsername(String username) { return employeesDao.findByUsername(username); }

    public List<Employee> findAllByOrderByEmployeeIdDesc() {
        return employeesDao.findAllByOrderByEmployeeIdDesc();
    }

    public Page<Employee> findAllByOrderByEmployeeIdDesc(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return employeesDao.findAllByOrderByEmployeeIdDesc(pageable);
    }

    public Employee save(Employee employee) {
        return employeesDao.save(employee);
    }

    public boolean delete(Long employeeId) {
        Employee employeeToDelete = new EmployeeBuilder()
                .withEmployeeId(employeeId)
                .build();

        if (!ordersDao.findAllByEmployee(employeeToDelete).isEmpty()) {
            return false;
        }

        employeesDao.delete(employeeToDelete);
        return true;
    }
}
