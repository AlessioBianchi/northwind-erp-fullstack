package com.github.alessiobianchi.erp.model;

import jakarta.persistence.*;
import org.jspecify.annotations.Nullable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;

public class User implements UserDetails {

    private Employee employee;

    public User(Employee employee) {
        this.employee = employee;
    }

    @Override
    public boolean isAccountNonExpired() {
        // Not needed for the exercise
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // Not needed for the exercise
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // Not needed for the exercise
        return true;
    }

    @Override
    public boolean isEnabled() {
        // Not needed for the exercise
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getUsername() {
        return employee.getUsername();
    }

    @Override
    public String getPassword() {
        return employee.getPassword();
    }

    public Employee getEmployee(){
        return employee;
    }
}
