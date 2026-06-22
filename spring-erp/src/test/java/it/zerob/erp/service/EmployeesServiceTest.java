package it.zerob.erp.service;

import it.zerob.erp.dao.OrdersDAO;
import it.zerob.erp.dao.EmployeesDAO;
import it.zerob.erp.model.*;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeesServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmployeesDAO employeesDao;

    @Mock
    private OrdersDAO ordersDao;

    @InjectMocks
    private EmployeesService employeesService;

    private Employee sampleEmployee;

    @BeforeEach
    void setUp() {
        sampleEmployee = new EmployeeBuilder()
                .withEmployeeId(1L)
                .withFirstname("John")
                .withLastname("Doe")
                .build();
    }

    @Test
    void findAllByOrderByEmployeeIdDesc_ShouldReturnList() {
        // Arrange
        when(employeesDao.findAllByOrderByEmployeeIdDesc()).thenReturn(List.of(sampleEmployee));

        // Act
        List<Employee> result = employeesService.findAllByOrderByEmployeeIdDesc();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(employeesDao, times(1)).findAllByOrderByEmployeeIdDesc();
    }

    @Test
    void findAllByOrderByEmployeeIdDesc_WithPagination_ShouldReturnPage() {
        // Arrange
        int pageNum = 0;
        int pageSize = 5;
        Page<Employee> employeePage = new PageImpl<>(List.of(sampleEmployee));
        when(employeesDao.findAllByOrderByEmployeeIdDesc(any(Pageable.class))).thenReturn(employeePage);

        // Act
        Page<Employee> result = employeesService.findAllByOrderByEmployeeIdDesc(pageNum, pageSize);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(employeesDao).findAllByOrderByEmployeeIdDesc(PageRequest.of(pageNum, pageSize));
    }

    @Test
    void create_ShouldReturnCreatedEmployee() {
        // Arrange
        when(passwordEncoder.encode(any())).thenReturn("hashed_password");
        when(employeesDao.save(any(Employee.class))).thenReturn(sampleEmployee);

        // Act
        Employee created = employeesService.create(new Employee());

        // Assert
        assertNotNull(created);
        assertEquals(1L, created.getEmployeeId());
        verify(employeesDao).save(any(Employee.class));
    }

    @Test
    void update_ShouldReturnUpdatedEmployee() {
        // Arrange
        Long employeeId = 1L;
        String newEmployeeName = "Test";
        Employee employeeUpdated = new EmployeeBuilder()
                .withEmployeeId(employeeId)
                .withFirstname(newEmployeeName)
                .build();
        when(employeesDao.save(any(Employee.class))).thenReturn(employeeUpdated);

        // Act
        Employee updated = employeesService.update(employeeId, new Employee());

        // Assert
        assertNotNull(updated);
        assertEquals(1L, updated.getEmployeeId());
        assertEquals("Test", updated.getFirstname());
        verify(employeesDao).save(any(Employee.class));
    }

    @Test
    void delete_WhenEmployeeHasOrders_ShouldReturnFalse() {
        // Arrange
        Long employeeId = 1L;
        when(ordersDao.findAllByEmployee(any(Employee.class))).thenReturn(List.of(new Order()));

        // Act
        boolean result = employeesService.delete(employeeId);

        // Assert
        assertFalse(result);
        verify(employeesDao, never()).delete(any(Employee.class));
    }

    @Test
    void delete_WhenEmployeeHasNoOrders_ShouldReturnTrue() {
        // Arrange
        Long employeeId = 1L;
        when(ordersDao.findAllByEmployee(any(Employee.class))).thenReturn(Collections.emptyList());

        // Act
        boolean result = employeesService.delete(employeeId);

        // Assert
        assertTrue(result);
        verify(employeesDao, times(1)).delete(any(Employee.class));
    }
}