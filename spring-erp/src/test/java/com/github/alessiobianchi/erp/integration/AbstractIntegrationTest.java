package com.github.alessiobianchi.erp.integration;

import com.github.alessiobianchi.erp.TestcontainersConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Full application against a throwaway PostgreSQL (Flyway V1–V5 + Northwind seed data).
// No @Transactional rollback on purpose: requests must commit like in production (auditing, constraints).
// Tests create the rows they change, so they don't depend on each other.
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
abstract class AbstractIntegrationTest {

    static final String SEED_PASSWORD = "password123";
    static final String MANAGER = "manager2";
    static final String EMPLOYEE = "employee1";

    @Autowired
    MockMvc mockMvc;

    MockHttpSession login(String username, String password) throws Exception {
        return (MockHttpSession) mockMvc.perform(post("/api/auth/login")
                        .param("username", username)
                        .param("password", password))
                .andExpect(status().isOk())
                .andReturn()
                .getRequest()
                .getSession();
    }
}
