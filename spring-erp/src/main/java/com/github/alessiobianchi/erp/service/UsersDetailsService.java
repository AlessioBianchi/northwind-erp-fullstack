package com.github.alessiobianchi.erp.service;

import com.github.alessiobianchi.erp.dao.EmployeesDAO;
import com.github.alessiobianchi.erp.model.Employee;
import com.github.alessiobianchi.erp.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UsersDetailsService implements UserDetailsService {

    private final EmployeesDAO dao;

    public UsersDetailsService(EmployeesDAO dao) {
        this.dao = dao;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return dao.findByUsername(username)
                .map(User::new)
                .orElseThrow(() -> new UsernameNotFoundException("Incorrect credentials."));
    }
}
