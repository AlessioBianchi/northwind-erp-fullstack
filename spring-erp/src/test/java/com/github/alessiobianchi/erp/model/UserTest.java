package com.github.alessiobianchi.erp.model;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserTest {

    @Test
    void getAuthorities_WhenEmployeeReportsToNoOne_ShouldIncludeManager() {
        User user = new User(new EmployeeBuilder().build());

        assertEquals(List.of("ROLE_USER", "ROLE_MANAGER"), authorities(user));
    }

    @Test
    void getAuthorities_WhenEmployeeHasAManager_ShouldBeUserOnly() {
        Employee manager = new EmployeeBuilder().withEmployeeId(1).build();
        User user = new User(new EmployeeBuilder().withReportsTo(manager).build());

        assertEquals(List.of("ROLE_USER"), authorities(user));
    }

    private static List<String> authorities(User user) {
        return user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
    }
}
