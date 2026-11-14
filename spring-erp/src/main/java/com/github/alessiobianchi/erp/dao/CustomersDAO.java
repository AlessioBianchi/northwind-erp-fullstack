package com.github.alessiobianchi.erp.dao;

import com.github.alessiobianchi.erp.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CustomersDAO extends CrudRepository<Customer, Integer> {

    List<Customer> findAllByOrderByCustomerIdDesc();

    Page<Customer> findAllByOrderByCustomerIdDesc(Pageable pageable);

    @Query("""
            select c from Customer c
            where lower(c.customerCode) like lower(concat('%', :search, '%'))
               or lower(c.companyName) like lower(concat('%', :search, '%'))
            order by c.customerId desc""")
    Page<Customer> search(@Param("search") String search, Pageable pageable);
}
