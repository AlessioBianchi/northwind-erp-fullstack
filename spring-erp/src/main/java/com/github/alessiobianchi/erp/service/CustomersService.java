package com.github.alessiobianchi.erp.service;

import com.github.alessiobianchi.erp.model.Customer;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CustomersService {

    List<Customer> findAllByOrderByCustomerIdDesc();

    Page<Customer> findAllByOrderByCustomerIdDesc(int pageNumber, int pageSize);

    Customer create(Customer customer);

    Customer update(int customerId, Customer customer);

    void delete(int customerId);
}
