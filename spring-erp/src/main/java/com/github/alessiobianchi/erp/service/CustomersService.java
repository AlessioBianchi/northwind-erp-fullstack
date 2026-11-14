package com.github.alessiobianchi.erp.service;

import com.github.alessiobianchi.erp.dto.CustomerDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CustomersService {

    List<CustomerDTO> findAllByOrderByCustomerIdDesc();

    Page<CustomerDTO> findAllByOrderByCustomerIdDesc(int pageNumber, int pageSize, String search);

    CustomerDTO create(CustomerDTO customerDto);

    CustomerDTO update(int customerId, CustomerDTO customerDto);

    void delete(int customerId);
}
