package com.github.alessiobianchi.erp.service.impl;

import com.github.alessiobianchi.erp.dao.CustomersDAO;
import com.github.alessiobianchi.erp.dao.OrdersDAO;
import com.github.alessiobianchi.erp.exception.ConflictException;
import com.github.alessiobianchi.erp.exception.ResourceNotFoundException;
import com.github.alessiobianchi.erp.model.Customer;
import com.github.alessiobianchi.erp.model.CustomerBuilder;
import com.github.alessiobianchi.erp.service.CustomersService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class CustomersServiceImpl implements CustomersService {

    private final CustomersDAO customersDao;
    private final OrdersDAO ordersDao;

    public CustomersServiceImpl(CustomersDAO customersDao, OrdersDAO ordersDao) {
        this.customersDao = customersDao;
        this.ordersDao = ordersDao;
    }

    @Override
    public List<Customer> findAllByOrderByCustomerIdDesc() {
        return customersDao.findAllByOrderByCustomerIdDesc();
    }

    @Override
    public Page<Customer> findAllByOrderByCustomerIdDesc(int pageNumber, int pageSize, String search) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return StringUtils.hasText(search)
                ? customersDao.search(search.trim(), pageable)
                : customersDao.findAllByOrderByCustomerIdDesc(pageable);
    }

    @Override
    public Customer create(Customer customer) {
        return customersDao.save(customer);
    }

    @Override
    public Customer update(int customerId, Customer customer) {
        Customer existing = customersDao.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        Customer customerToUpdate = new CustomerBuilder(customer)
                .withCustomerId(customerId)
                .withCreatedAt(existing.getCreatedAt())
                .build();

        return customersDao.save(customerToUpdate);
    }

    @Override
    public void delete(int customerId) {
        Customer customerToDelete = customersDao.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        if (!ordersDao.findAllByCustomer(customerToDelete).isEmpty()) {
            throw new ConflictException("This customer is connected to an order. It can't be deleted.");
        }

        customersDao.delete(customerToDelete);
    }
}
