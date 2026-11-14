package com.github.alessiobianchi.erp.service.impl;

import com.github.alessiobianchi.erp.dao.EmployeesDAO;
import com.github.alessiobianchi.erp.dao.OrdersDAO;
import com.github.alessiobianchi.erp.dto.EmployeeDTO;
import com.github.alessiobianchi.erp.exception.BadRequestException;
import com.github.alessiobianchi.erp.exception.ConflictException;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    private EmployeesServiceImpl employeesService;

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
        Page<EmployeeDTO> result = employeesService.findAllByOrderByEmployeeIdDesc(pageNum, pageSize, null);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(employeesDao).findAllByOrderByEmployeeIdDesc(PageRequest.of(pageNum, pageSize));
    }

    @Test
    void findAllByOrderByEmployeeIdDesc_WithBlankSearch_ShouldReturnUnfilteredPage() {
        // Arrange
        when(employeesDao.findAllByOrderByEmployeeIdDesc(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(sampleEmployee)));

        // Act
        Page<EmployeeDTO> result = employeesService.findAllByOrderByEmployeeIdDesc(0, 5, "   ");

        // Assert
        assertEquals(1, result.getTotalElements());
        verify(employeesDao).findAllByOrderByEmployeeIdDesc(PageRequest.of(0, 5));
        verify(employeesDao, never()).search(any(), any());
    }

    @Test
    void findAllByOrderByEmployeeIdDesc_WithSearch_ShouldSearchWithTrimmedText() {
        // Arrange
        when(employeesDao.search(eq("chai"), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(sampleEmployee)));

        // Act
        Page<EmployeeDTO> result = employeesService.findAllByOrderByEmployeeIdDesc(0, 5, "  chai ");

        // Assert
        assertEquals(1, result.getTotalElements());
        verify(employeesDao).search("chai", PageRequest.of(0, 5));
        verify(employeesDao, never()).findAllByOrderByEmployeeIdDesc(any(Pageable.class));
    }

    @Test
    void create_ShouldReturnCreatedEmployee() {
        // Arrange
        when(passwordEncoder.encode(any())).thenReturn("hashed_password");
        when(employeesDao.save(any(Employee.class))).thenReturn(sampleEmployee);

        // Act
        EmployeeDTO created = employeesService.create(new EmployeeBuilder().withPassword("secret123").build());

        // Assert
        assertNotNull(created);
        assertEquals(1, created.getEmployeeId());
        verify(employeesDao).save(any(Employee.class));
    }

    @Test
    void findByUsername_WhenEmployeeExists_ShouldReturnDto() {
        // Arrange
        Employee employee = new EmployeeBuilder(sampleEmployee)
                .withUsername("jdoe")
                .build();
        when(employeesDao.findByUsername("jdoe")).thenReturn(Optional.of(employee));

        // Act
        EmployeeDTO result = employeesService.findByUsername("jdoe");

        // Assert
        assertEquals(1, result.getEmployeeId());
        assertEquals("jdoe", result.getUsername());
    }

    @Test
    void findByUsername_WhenEmployeeNotFound_ShouldThrowNotFound() {
        // Arrange
        when(employeesDao.findByUsername("ghost")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> employeesService.findByUsername("ghost"));
    }

    @Test
    void create_ShouldSaveEncodedPassword() {
        // Arrange
        when(passwordEncoder.encode("secret123")).thenReturn("hashed_password");
        when(employeesDao.save(any(Employee.class))).thenReturn(sampleEmployee);
        ArgumentCaptor<Employee> captor = ArgumentCaptor.forClass(Employee.class);

        // Act
        employeesService.create(new EmployeeBuilder().withPassword("secret123").build());

        // Assert
        verify(employeesDao).save(captor.capture());
        assertEquals("hashed_password", captor.getValue().getPassword());
    }

    @Test
    void create_WhenPasswordIsNull_ShouldThrow() {
        // Act & Assert
        assertThrows(BadRequestException.class, () -> employeesService.create(sampleEmployee));
        verify(employeesDao, never()).save(any(Employee.class));
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void create_WhenPasswordIsBlank_ShouldThrow() {
        // Arrange
        Employee incoming = new EmployeeBuilder(sampleEmployee)
                .withPassword(" ")
                .build();

        // Act & Assert
        assertThrows(BadRequestException.class, () -> employeesService.create(incoming));
        verify(employeesDao, never()).save(any(Employee.class));
    }

    @Test
    void create_WhenPasswordTooShort_ShouldThrow() {
        // Arrange
        Employee incoming = new EmployeeBuilder(sampleEmployee)
                .withPassword("short")
                .build();

        // Act & Assert
        BadRequestException ex = assertThrows(BadRequestException.class, () -> employeesService.create(incoming));
        assertEquals("Password must be at least 8 characters.", ex.getMessage());
        verify(employeesDao, never()).save(any(Employee.class));
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void create_WhenPasswordOver72Bytes_ShouldThrow() {
        // Arrange: 40 characters, but 80 bytes in UTF-8
        Employee incoming = new EmployeeBuilder(sampleEmployee)
                .withPassword("é".repeat(40))
                .build();

        // Act & Assert
        assertThrows(BadRequestException.class, () -> employeesService.create(incoming));
        verify(employeesDao, never()).save(any(Employee.class));
        verifyNoInteractions(passwordEncoder);
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
    void update_WhenNewPasswordTooShort_ShouldThrow() {
        // Arrange
        int employeeId = 1;
        Employee incoming = new EmployeeBuilder(sampleEmployee)
                .withPassword("short")
                .build();
        when(employeesDao.findById(employeeId)).thenReturn(Optional.of(sampleEmployee));

        // Act & Assert
        assertThrows(BadRequestException.class, () -> employeesService.update(employeeId, incoming));
        verify(employeesDao, never()).save(any(Employee.class));
        verifyNoInteractions(passwordEncoder);
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
    void delete_WhenEmployeeNotFound_ShouldThrowNotFound() {
        // Arrange
        when(employeesDao.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> employeesService.delete(1));
        verify(employeesDao, never()).delete(any(Employee.class));
    }

    @Test
    void delete_WhenEmployeeHasOrders_ShouldThrowConflict() {
        // Arrange
        Employee employee = new EmployeeBuilder().withEmployeeId(1).build();
        when(employeesDao.findById(1)).thenReturn(Optional.of(employee));
        when(ordersDao.findAllByEmployee(employee)).thenReturn(List.of(new Order()));

        // Act & Assert
        assertThrows(ConflictException.class, () -> employeesService.delete(1));
        verify(employeesDao, never()).delete(any(Employee.class));
    }

    @Test
    void delete_WhenEmployeeHasNoOrders_ShouldDelete() {
        // Arrange
        Employee employee = new EmployeeBuilder().withEmployeeId(1).build();
        when(employeesDao.findById(1)).thenReturn(Optional.of(employee));
        when(ordersDao.findAllByEmployee(employee)).thenReturn(List.of());

        // Act
        employeesService.delete(1);

        // Assert
        verify(employeesDao).delete(employee);
    }

    @Test
    void changeOwnPassword_ShouldSaveEncodedNewPassword() {
        // Arrange
        Employee existing = new EmployeeBuilder(sampleEmployee)
                .withUsername("jdoe")
                .withPassword("old_hash")
                .build();
        when(employeesDao.findByUsername("jdoe")).thenReturn(Optional.of(existing));
        when(passwordEncoder.matches("oldPassword1", "old_hash")).thenReturn(true);
        when(passwordEncoder.encode("newPassword1")).thenReturn("new_hash");

        // Act
        employeesService.changeOwnPassword("jdoe", "oldPassword1", "newPassword1");

        // Assert
        ArgumentCaptor<Employee> captor = ArgumentCaptor.forClass(Employee.class);
        verify(employeesDao).save(captor.capture());
        assertEquals("new_hash", captor.getValue().getPassword());
        assertEquals(1, captor.getValue().getEmployeeId());
        assertEquals("John", captor.getValue().getFirstName());
    }

    @Test
    void changeOwnPassword_WhenCurrentPasswordWrong_ShouldThrowBadRequest() {
        // Arrange
        Employee existing = new EmployeeBuilder(sampleEmployee)
                .withUsername("jdoe")
                .withPassword("old_hash")
                .build();
        when(employeesDao.findByUsername("jdoe")).thenReturn(Optional.of(existing));
        when(passwordEncoder.matches("wrongPassword", "old_hash")).thenReturn(false);

        // Act & Assert
        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> employeesService.changeOwnPassword("jdoe", "wrongPassword", "newPassword1"));
        assertEquals("Current password is incorrect.", ex.getMessage());
        verify(employeesDao, never()).save(any(Employee.class));
    }

    @Test
    void changeOwnPassword_WhenNewPasswordTooShort_ShouldThrowBadRequest() {
        // Arrange
        Employee existing = new EmployeeBuilder(sampleEmployee)
                .withUsername("jdoe")
                .withPassword("old_hash")
                .build();
        when(employeesDao.findByUsername("jdoe")).thenReturn(Optional.of(existing));
        when(passwordEncoder.matches("oldPassword1", "old_hash")).thenReturn(true);

        // Act & Assert
        assertThrows(BadRequestException.class,
                () -> employeesService.changeOwnPassword("jdoe", "oldPassword1", "short"));
        verify(employeesDao, never()).save(any(Employee.class));
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void changeOwnPassword_WhenUserNotFound_ShouldThrowNotFound() {
        // Arrange
        when(employeesDao.findByUsername("ghost")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> employeesService.changeOwnPassword("ghost", "oldPassword1", "newPassword1"));
        verify(employeesDao, never()).save(any(Employee.class));
    }
}
