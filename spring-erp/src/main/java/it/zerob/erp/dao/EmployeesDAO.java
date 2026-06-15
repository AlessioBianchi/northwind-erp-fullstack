package it.zerob.erp.dao;

import it.zerob.erp.model.Employee;
import it.zerob.erp.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeesDAO extends CrudRepository<Employee, Long> {

    Optional<Employee> findByUsername(String username);

    List<Employee> findAllByOrderByEmployeeIdDesc();

    Page<Employee> findAllByOrderByEmployeeIdDesc(Pageable pageable);
}
