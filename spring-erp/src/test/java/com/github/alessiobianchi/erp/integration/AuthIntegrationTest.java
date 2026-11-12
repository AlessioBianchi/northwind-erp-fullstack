package com.github.alessiobianchi.erp.integration;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthIntegrationTest extends AbstractIntegrationTest {

    @Test
    void login_AsEmployee_ShouldReturnUserInfoWithoutManagerFlag() throws Exception {
        mockMvc.perform(post("/api/auth/login").param("username", EMPLOYEE).param("password", SEED_PASSWORD))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(EMPLOYEE))
                .andExpect(jsonPath("$.isManager").value(false));
    }

    @Test
    void login_AsManager_ShouldSetManagerFlag() throws Exception {
        mockMvc.perform(post("/api/auth/login").param("username", MANAGER).param("password", SEED_PASSWORD))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isManager").value(true));
    }

    @Test
    void login_WithWrongPassword_ShouldReturn401() throws Exception {
        mockMvc.perform(post("/api/auth/login").param("username", EMPLOYEE).param("password", "wrong-password"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid username or password"));
    }

    @Test
    void login_WithJsonBody_ShouldReturn401() throws Exception {
        // formLogin only reads form fields; the Swagger login bug sent JSON.
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + EMPLOYEE + "\",\"password\":\"" + SEED_PASSWORD + "\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpoint_WithoutSession_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/v1/orders"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpoint_WithSession_ShouldReturn200() throws Exception {
        MockHttpSession session = login(EMPLOYEE, SEED_PASSWORD);

        mockMvc.perform(get("/api/v1/orders").session(session))
                .andExpect(status().isOk());
    }

    @Test
    void logout_ShouldInvalidateTheSession() throws Exception {
        MockHttpSession session = login(EMPLOYEE, SEED_PASSWORD);

        mockMvc.perform(post("/api/auth/logout").session(session))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/orders").session(session))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void writeRequest_WithoutCsrfToken_ShouldReturn403() throws Exception {
        MockHttpSession session = login(EMPLOYEE, SEED_PASSWORD);

        mockMvc.perform(post("/api/v1/shippers").session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"companyName\":\"No CSRF Shipper\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void writeRequest_WithCsrfToken_ShouldPass() throws Exception {
        MockHttpSession session = login(EMPLOYEE, SEED_PASSWORD);

        mockMvc.perform(post("/api/v1/shippers").session(session).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"companyName\":\"CSRF Shipper\"}"))
                .andExpect(status().isCreated());
    }
}
