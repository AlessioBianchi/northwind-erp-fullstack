package com.github.alessiobianchi.erp.controller;

import com.github.alessiobianchi.erp.config.AppConfig;
import com.github.alessiobianchi.erp.config.PasswordConfig;
import com.github.alessiobianchi.erp.exception.BadRequestException;
import com.github.alessiobianchi.erp.model.Employee;
import com.github.alessiobianchi.erp.service.EmployeesService;
import com.github.alessiobianchi.erp.service.UsersDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmployeesController.class)
@Import({AppConfig.class, PasswordConfig.class})
class EmployeesControllerSecurityTest {

    private static final String EMPLOYEE_JSON = "{\"employeeId\":1,\"firstName\":\"John\",\"lastName\":\"Doe\"}";
    private static final String CHANGE_PASSWORD_JSON = "{\"currentPassword\":\"password123\",\"newPassword\":\"newPassword1\"}";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeesService employeesService;

    @MockitoBean
    private UsersDetailsService usersDetailsService;

    @Test
    @WithMockUser(roles = "USER")
    void list_AsEmployee_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/v1/employees"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void create_AsEmployee_ShouldReturn403() throws Exception {
        mockMvc.perform(post("/api/v1/employees").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(EMPLOYEE_JSON))
                .andExpect(status().isForbidden());

        verifyNoInteractions(employeesService);
    }

    @Test
    @WithMockUser(roles = "USER")
    void update_AsEmployee_ShouldReturn403() throws Exception {
        mockMvc.perform(put("/api/v1/employees/1").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(EMPLOYEE_JSON))
                .andExpect(status().isForbidden());

        verifyNoInteractions(employeesService);
    }

    @Test
    @WithMockUser(roles = "USER")
    void delete_AsEmployee_ShouldReturn403() throws Exception {
        mockMvc.perform(delete("/api/v1/employees/1").with(csrf()))
                .andExpect(status().isForbidden());

        verifyNoInteractions(employeesService);
    }

    @Test
    @WithMockUser(roles = {"USER", "MANAGER"})
    void update_AsManager_ShouldReturn200() throws Exception {
        mockMvc.perform(put("/api/v1/employees/1").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(EMPLOYEE_JSON))
                .andExpect(status().isOk());

        verify(employeesService).update(eq(1), any(Employee.class));
    }

    @Test
    @WithMockUser(roles = {"USER", "MANAGER"})
    void create_AsManagerWithoutEmployeeId_ShouldReturn201() throws Exception {
        mockMvc.perform(post("/api/v1/employees").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"username\":\"jdoe\",\"password\":\"password123\"}"))
                .andExpect(status().isCreated());

        verify(employeesService).create(argThat(employee -> employee.getEmployeeId() == null));
    }

    @Test
    @WithMockUser(username = "employee1", roles = "USER")
    void changeOwnPassword_AsEmployee_ShouldReturn204ForOwnAccount() throws Exception {
        mockMvc.perform(put("/api/v1/employees/me/password").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(CHANGE_PASSWORD_JSON))
                .andExpect(status().isNoContent());

        verify(employeesService).changeOwnPassword("employee1", "password123", "newPassword1");
    }

    @Test
    void changeOwnPassword_WhenNotLoggedIn_ShouldReturn401() throws Exception {
        mockMvc.perform(put("/api/v1/employees/me/password").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(CHANGE_PASSWORD_JSON))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(employeesService);
    }

    @Test
    @WithMockUser(roles = "USER")
    void changeOwnPassword_WhenFieldsMissing_ShouldReturn400() throws Exception {
        mockMvc.perform(put("/api/v1/employees/me/password").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(employeesService);
    }

    @Test
    @WithMockUser(username = "employee1", roles = "USER")
    void changeOwnPassword_WhenCurrentPasswordWrong_ShouldReturn400WithMessage() throws Exception {
        doThrow(new BadRequestException("Current password is incorrect."))
                .when(employeesService).changeOwnPassword(anyString(), anyString(), anyString());

        mockMvc.perform(put("/api/v1/employees/me/password").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(CHANGE_PASSWORD_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Current password is incorrect."));
    }
}
