package com.github.alessiobianchi.erp.service.impl;

import com.github.alessiobianchi.erp.converter.CustomerBeanToDtoConverter;
import com.github.alessiobianchi.erp.converter.CustomerDtoToBeanConverter;
import com.github.alessiobianchi.erp.dao.CustomersDAO;
import com.github.alessiobianchi.erp.dao.OrdersDAO;
import com.github.alessiobianchi.erp.dto.CustomerDTO;
import com.github.alessiobianchi.erp.exception.ConflictException;
import com.github.alessiobianchi.erp.exception.ResourceNotFoundException;
import com.github.alessiobianchi.erp.model.Customer;
import com.github.alessiobianchi.erp.model.CustomerBuilder;
import com.github.alessiobianchi.erp.service.CustomersService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CustomersServiceImpl implements CustomersService {

    private final CustomersDAO customersDao;
    private final OrdersDAO ordersDao;

    public CustomersServiceImpl(CustomersDAO customersDao, OrdersDAO ordersDao) {
        this.customersDao = customersDao;
        this.ordersDao = ordersDao;
    }

    @Override
    public List<CustomerDTO> findAllByOrderByCustomerIdDesc() {
        return customersDao.findAllByOrderByCustomerIdDesc().stream()
                .map(CustomerBeanToDtoConverter::convert)
                .toList();
    }

    @Override
    public Page<CustomerDTO> findAllByOrderByCustomerIdDesc(int pageNumber, int pageSize, String search) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Customer> page = StringUtils.hasText(search)
                ? customersDao.search(search.trim(), pageable)
                : customersDao.findAllByOrderByCustomerIdDesc(pageable);
        return page.map(CustomerBeanToDtoConverter::convert);
    }

    @Override
    @Transactional
    public CustomerDTO create(CustomerDTO customerDto) {
        return CustomerBeanToDtoConverter.convert(customersDao.save(CustomerDtoToBeanConverter.convert(customerDto)));
    }

    @Override
    @Transactional
    public CustomerDTO update(int customerId, CustomerDTO customerDto) {
        Customer existing = customersDao.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        Customer customerToUpdate = new CustomerBuilder(CustomerDtoToBeanConverter.convert(customerDto))
                .withCustomerId(customerId)
                .withCreatedAt(existing.getCreatedAt())
                .build();

        return CustomerBeanToDtoConverter.convert(customersDao.save(customerToUpdate));
    }

    @Override
    @Transactional
    public void delete(int customerId) {
        Customer customerToDelete = customersDao.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        if (!ordersDao.findAllByCustomer(customerToDelete).isEmpty()) {
            throw new ConflictException("This customer is connected to an order. It can't be deleted.");
        }

        customersDao.delete(customerToDelete);
    }
}
