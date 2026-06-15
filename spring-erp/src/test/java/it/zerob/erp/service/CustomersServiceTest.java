package it.zerob.erp.service;

import it.zerob.erp.dao.CustomersDAO;
import it.zerob.erp.dao.OrdersDAO;
import it.zerob.erp.model.Customer;
import it.zerob.erp.model.CustomerBuilder;
import it.zerob.erp.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomersServiceTest {

    @Mock
    private CustomersDAO customersDao;

    @Mock
    private OrdersDAO ordersDao;

    @InjectMocks
    private CustomersService customersService;

    private Customer sampleCustomer;

    @BeforeEach
    void setUp() {
        sampleCustomer = new CustomerBuilder()
                .withCustomerId(1L)
                .build();
    }

    @Test
    void findAllByOrderByCustomerIdDesc_ShouldReturnList() {
        // Arrange
        when(customersDao.findAllByOrderByCustomerIdDesc()).thenReturn(List.of(sampleCustomer));

        // Act
        List<Customer> result = customersService.findAllByOrderByCustomerIdDesc();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(customersDao, times(1)).findAllByOrderByCustomerIdDesc();
    }

    @Test
    void findAllByOrderByCustomerIdDesc_WithPagination_ShouldReturnPage() {
        // Arrange
        int pageNum = 0;
        int pageSize = 10;
        Page<Customer> page = new PageImpl<>(List.of(sampleCustomer));
        when(customersDao.findAllByOrderByCustomerIdDesc(any(Pageable.class))).thenReturn(page);

        // Act
        Page<Customer> result = customersService.findAllByOrderByCustomerIdDesc(pageNum, pageSize);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(customersDao).findAllByOrderByCustomerIdDesc(PageRequest.of(pageNum, pageSize));
    }

    @Test
    void save_ShouldReturnSavedCustomer() {
        // Arrange
        when(customersDao.save(any(Customer.class))).thenReturn(sampleCustomer);

        // Act
        Customer saved = customersService.save(new Customer());

        // Assert
        assertNotNull(saved);
        assertEquals(1L, saved.getCustomerId());
        verify(customersDao).save(any(Customer.class));
    }

    @Test
    void delete_WhenCustomerHasOrders_ShouldReturnFalse() {
        // Arrange
        Long customerId = 1L;
        when(ordersDao.findAllByCustomer(any(Customer.class))).thenReturn(List.of(new Order()));

        // Act
        boolean result = customersService.delete(customerId);

        // Assert
        assertFalse(result);
        verify(customersDao, never()).delete(any(Customer.class));
    }

    @Test
    void delete_WhenCustomerHasNoOrders_ShouldReturnTrue() {
        // Arrange
        Long customerId = 1L;
        when(ordersDao.findAllByCustomer(any(Customer.class))).thenReturn(Collections.emptyList());

        // Act
        boolean result = customersService.delete(customerId);

        // Assert
        assertTrue(result);
        verify(customersDao, times(1)).delete(any(Customer.class));
    }
}