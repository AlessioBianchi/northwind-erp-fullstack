package com.github.alessiobianchi.erp.service;

import com.github.alessiobianchi.erp.dao.CustomersDAO;
import com.github.alessiobianchi.erp.dao.OrdersDAO;
import com.github.alessiobianchi.erp.model.Customer;
import com.github.alessiobianchi.erp.model.CustomerBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomersService {

    private final CustomersDAO customersDao;
    private final OrdersDAO ordersDao;

    public CustomersService(CustomersDAO customersDao, OrdersDAO ordersDao) {
        this.customersDao = customersDao;
        this.ordersDao = ordersDao;
    }

    public List<Customer> findAllByOrderByCustomerIdDesc() {
        return customersDao.findAllByOrderByCustomerIdDesc();
    }

    public Page<Customer> findAllByOrderByCustomerIdDesc(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return customersDao.findAllByOrderByCustomerIdDesc(pageable);
    }

    public Customer create(Customer customer) {
        return customersDao.save(customer);
    }

    public Customer update(int customerId, Customer customer) {
        Customer customerToUpdate = new CustomerBuilder(customer)
                .withCustomerId(customerId)
                .build();

        return customersDao.save(customerToUpdate);
    }

    public boolean delete(int customerId) {
        Customer customerToDelete = new CustomerBuilder()
                .withCustomerId(customerId)
                .build();

        if (!ordersDao.findAllByCustomer(customerToDelete).isEmpty()) {
            return false;
        }

        customersDao.delete(customerToDelete);
        return true;
    }
}
