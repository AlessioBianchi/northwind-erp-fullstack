package com.github.alessiobianchi.erp.dao;

import com.github.alessiobianchi.erp.model.Employee;
import com.github.alessiobianchi.erp.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmployeesDAO extends CrudRepository<Employee, Integer> {

    Optional<Employee> findByUsername(String username);

    List<Employee> findAllByOrderByEmployeeIdDesc();

    Page<Employee> findAllByOrderByEmployeeIdDesc(Pageable pageable);

    @Query("""
            select e from Employee e
            where lower(e.firstName) like lower(concat('%', :search, '%'))
               or lower(e.lastName) like lower(concat('%', :search, '%'))
               or lower(e.title) like lower(concat('%', :search, '%'))
            order by e.employeeId desc""")
    Page<Employee> search(@Param("search") String search, Pageable pageable);
}
