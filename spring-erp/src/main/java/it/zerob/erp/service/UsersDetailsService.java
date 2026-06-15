package it.zerob.erp.service;

import it.zerob.erp.dao.EmployeesDAO;
import it.zerob.erp.model.Employee;
import it.zerob.erp.model.User;
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
