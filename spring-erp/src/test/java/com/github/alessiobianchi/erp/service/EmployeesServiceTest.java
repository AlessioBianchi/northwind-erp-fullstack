package com.github.alessiobianchi.erp.service;

import com.github.alessiobianchi.erp.dao.OrdersDAO;
import com.github.alessiobianchi.erp.dao.EmployeesDAO;
import com.github.alessiobianchi.erp.dto.EmployeeDTO;
import com.github.alessiobianchi.erp.exception.ResourceNotFoundException;
import com.github.alessiobianchi.erp.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
import java.util.Optional;

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
                .withEmployeeId(1)
                .withFirstname("John")
                .withLastname("Doe")
                .build();
    }

    @Test
    void findAllByOrderByEmployeeIdDesc_ShouldReturnList() {
        // Arrange
        when(employeesDao.findAllByOrderByEmployeeIdDesc()).thenReturn(List.of(sampleEmployee));

        // Act
        List<EmployeeDTO> result = employeesService.findAllByOrderByEmployeeIdDesc();

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
        Page<EmployeeDTO> result = employeesService.findAllByOrderByEmployeeIdDesc(pageNum, pageSize);

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
        EmployeeDTO created = employeesService.create(new Employee());

        // Assert
        assertNotNull(created);
        assertEquals(1, created.getEmployeeId());
        verify(employeesDao).save(any(Employee.class));
    }

    @Test
    void update_ShouldReturnUpdatedEmployee() {
        // Arrange
        int employeeId = 1;
        String newEmployeeName = "Test";
        Employee employeeUpdated = new EmployeeBuilder()
                .withEmployeeId(employeeId)
                .withFirstname(newEmployeeName)
                .build();
        when(employeesDao.findById(employeeId)).thenReturn(Optional.of(sampleEmployee));
        when(employeesDao.save(any(Employee.class))).thenReturn(employeeUpdated);

        // Act
        EmployeeDTO updated = employeesService.update(employeeId, new Employee());

        // Assert
        assertNotNull(updated);
        assertEquals(1, updated.getEmployeeId());
        assertEquals("Test", updated.getFirstName());
        verify(employeesDao).save(any(Employee.class));
    }

    @Test
    void update_WhenPasswordIsBlank_ShouldKeepOldPassword() {
        // Arrange
        int employeeId = 1;
        Employee existing = new EmployeeBuilder(sampleEmployee)
                .withPassword("old_hash")
                .build();
        Employee incoming = new EmployeeBuilder(sampleEmployee)
                .withPassword("")
                .build();
        when(employeesDao.findById(employeeId)).thenReturn(Optional.of(existing));
        when(employeesDao.save(any(Employee.class))).thenReturn(existing);

        // Act
        employeesService.update(employeeId, incoming);

        // Assert
        ArgumentCaptor<Employee> captor = ArgumentCaptor.forClass(Employee.class);
        verify(employeesDao).save(captor.capture());
        assertEquals("old_hash", captor.getValue().getPassword());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void update_WhenPasswordIsProvided_ShouldEncodeNewPassword() {
        // Arrange
        int employeeId = 1;
        Employee existing = new EmployeeBuilder(sampleEmployee)
                .withPassword("old_hash")
                .build();
        Employee incoming = new EmployeeBuilder(sampleEmployee)
                .withPassword("new_password")
                .build();
        when(employeesDao.findById(employeeId)).thenReturn(Optional.of(existing));
        when(passwordEncoder.encode("new_password")).thenReturn("new_hash");
        when(employeesDao.save(any(Employee.class))).thenReturn(existing);

        // Act
        employeesService.update(employeeId, incoming);

        // Assert
        ArgumentCaptor<Employee> captor = ArgumentCaptor.forClass(Employee.class);
        verify(employeesDao).save(captor.capture());
        assertEquals("new_hash", captor.getValue().getPassword());
    }

    @Test
    void update_WhenEmployeeNotFound_ShouldThrow() {
        // Arrange
        int employeeId = 99;
        when(employeesDao.findById(employeeId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> employeesService.update(employeeId, new Employee()));
        verify(employeesDao, never()).save(any(Employee.class));
    }

    @Test
    void delete_WhenEmployeeHasOrders_ShouldReturnFalse() {
        // Arrange
        int employeeId = 1;
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
        int employeeId = 1;
        when(ordersDao.findAllByEmployee(any(Employee.class))).thenReturn(Collections.emptyList());

        // Act
        boolean result = employeesService.delete(employeeId);

        // Assert
        assertTrue(result);
        verify(employeesDao, times(1)).delete(any(Employee.class));
    }
}