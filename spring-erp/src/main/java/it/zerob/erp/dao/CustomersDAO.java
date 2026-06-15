package it.zerob.erp.dao;

import it.zerob.erp.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CustomersDAO extends CrudRepository<Customer, Long> {

    List<Customer> findAllByOrderByCustomerIdDesc();

    Page<Customer> findAllByOrderByCustomerIdDesc(Pageable pageable);
}
