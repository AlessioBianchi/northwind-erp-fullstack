package com.github.alessiobianchi.erp.service;

import com.github.alessiobianchi.erp.dao.EmployeesDAO;
import com.github.alessiobianchi.erp.model.Employee;
import com.github.alessiobianchi.erp.model.EmployeeBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsersDetailsServiceTest {

    @Mock
    private EmployeesDAO employeesDao;

    @InjectMocks
    private UsersDetailsService usersDetailsService;

    @Test
    void loadUserByUsername_WhenUserExists_ShouldReturnUserDetails() {
        // Arrange
        String username = "admin";
        Employee mockUser = new EmployeeBuilder()
                .withUsername(username)
                .withPassword("password123")
                .build();

        when(employeesDao.findByUsername(username)).thenReturn(Optional.of(mockUser));

        // Act
        UserDetails result = usersDetailsService.loadUserByUsername(username);

        // Assert
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        verify(employeesDao, times(1)).findByUsername(username);
    }

    @Test
    void loadUserByUsername_WhenUserDoesNotExist_ShouldThrowUsernameNotFoundException() {
        // Arrange
        String username = "unknown_user";

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> usersDetailsService.loadUserByUsername(username)
        );

        assertEquals("Incorrect credentials.", exception.getMessage());
        verify(employeesDao, times(1)).findByUsername(username);
    }
}