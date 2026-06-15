package it.zerob.erp.service;

import it.zerob.erp.dao.CustomersDAO;
import it.zerob.erp.dao.OrdersDAO;
import it.zerob.erp.model.Customer;
import it.zerob.erp.model.CustomerBuilder;
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

    public Customer save(Customer customer) {
        return customersDao.save(customer);
    }

    public boolean delete(Long customerId) {
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
