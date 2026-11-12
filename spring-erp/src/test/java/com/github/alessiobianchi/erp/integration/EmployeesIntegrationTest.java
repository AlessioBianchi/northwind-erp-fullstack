package com.github.alessiobianchi.erp.integration;

import com.github.alessiobianchi.erp.dao.EmployeesDAO;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;

import java.util.UUID;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class EmployeesIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private EmployeesDAO employeesDao;

    @Test
    void list_AsEmployee_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/v1/employees").session(login(EMPLOYEE, SEED_PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].password").doesNotExist());
    }

    @Test
    void create_AsEmployee_ShouldReturn403() throws Exception {
        mockMvc.perform(post("/api/v1/employees").session(login(EMPLOYEE, SEED_PASSWORD)).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(employeeJson(uniqueUsername(), SEED_PASSWORD)))
                .andExpect(status().isForbidden());
    }

    @Test
    void update_AsEmployee_ShouldNotChangeAnotherEmployeesPassword() throws Exception {
        int managerId = employeesDao.findByUsername(MANAGER).orElseThrow().getEmployeeId();

        mockMvc.perform(put("/api/v1/employees/" + managerId).session(login(EMPLOYEE, SEED_PASSWORD)).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"employeeId\":" + managerId + ",\"firstName\":\"X\",\"lastName\":\"Y\",\"password\":\"hacked-password\"}"))
                .andExpect(status().isForbidden());

        login(MANAGER, SEED_PASSWORD);
    }

    @Test
    void delete_AsEmployee_ShouldReturn403() throws Exception {
        int managerId = employeesDao.findByUsername(MANAGER).orElseThrow().getEmployeeId();

        mockMvc.perform(delete("/api/v1/employees/" + managerId).session(login(EMPLOYEE, SEED_PASSWORD)).with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    void create_AsManagerWithoutId_ShouldCreateAnEmployeeWhoCanLogIn() throws Exception {
        String username = uniqueUsername();

        mockMvc.perform(post("/api/v1/employees").session(login(MANAGER, SEED_PASSWORD)).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(employeeJson(username, "newEmployee1")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.employeeId").value(notNullValue()))
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.createdAt").value(notNullValue()));

        mockMvc.perform(post("/api/auth/login").param("username", username).param("password", "newEmployee1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isManager").value(false));
    }

    @Test
    void create_AsManagerWithShortPassword_ShouldReturn400() throws Exception {
        mockMvc.perform(post("/api/v1/employees").session(login(MANAGER, SEED_PASSWORD)).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(employeeJson(uniqueUsername(), "short")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Password must be at least 8 characters."));
    }

    @Test
    void update_AsManagerWithBlankPassword_ShouldKeepTheCurrentPassword() throws Exception {
        String username = uniqueUsername();
        MockHttpSession manager = login(MANAGER, SEED_PASSWORD);
        int employeeId = createEmployee(manager, username, "initialPass1");

        mockMvc.perform(put("/api/v1/employees/" + employeeId).session(manager).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(employeeJson(username, "").replace("{", "{\"employeeId\":" + employeeId + ",")))
                .andExpect(status().isOk());

        login(username, "initialPass1");
    }

    @Test
    void changeOwnPassword_ShouldReplaceTheOldPassword() throws Exception {
        String username = uniqueUsername();
        createEmployee(login(MANAGER, SEED_PASSWORD), username, "initialPass1");
        MockHttpSession session = login(username, "initialPass1");

        mockMvc.perform(put("/api/v1/employees/me/password").session(session).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"currentPassword\":\"initialPass1\",\"newPassword\":\"changedPass1\"}"))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/api/auth/login").param("username", username).param("password", "initialPass1"))
                .andExpect(status().isUnauthorized());
        login(username, "changedPass1");
    }

    @Test
    void changeOwnPassword_WithWrongCurrentPassword_ShouldReturn400() throws Exception {
        String username = uniqueUsername();
        createEmployee(login(MANAGER, SEED_PASSWORD), username, "initialPass1");

        mockMvc.perform(put("/api/v1/employees/me/password").session(login(username, "initialPass1")).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"currentPassword\":\"not-my-password\",\"newPassword\":\"changedPass1\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Current password is incorrect."));

        login(username, "initialPass1");
    }

    private int createEmployee(MockHttpSession manager, String username, String password) throws Exception {
        String body = mockMvc.perform(post("/api/v1/employees").session(manager).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(employeeJson(username, password)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(body, "$.employeeId");
    }

    private String employeeJson(String username, String password) {
        int managerId = employeesDao.findByUsername(MANAGER).orElseThrow().getEmployeeId();
        return "{\"firstName\":\"Test\",\"lastName\":\"Employee\",\"username\":\"" + username + "\","
                + "\"password\":\"" + password + "\",\"reportsTo\":{\"employeeId\":" + managerId + "}}";
    }

    private static String uniqueUsername() {
        return "it_" + UUID.randomUUID().toString().substring(0, 8);
    }
}
