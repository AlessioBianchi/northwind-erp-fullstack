package com.github.alessiobianchi.erp.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.mock.web.MockHttpSession;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Every paginated endpoint must reject bad page parameters with 400 (PageRequest.of used to throw → 500).
class PaginationIntegrationTest extends AbstractIntegrationTest {

    private MockHttpSession session;

    @BeforeEach
    void setUp() throws Exception {
        session = login(EMPLOYEE, SEED_PASSWORD);
    }

    @ParameterizedTest
    @ValueSource(strings = {"customers", "employees", "orders", "products", "suppliers"})
    void negativePageNumber_ShouldReturn400(String resource) throws Exception {
        mockMvc.perform(get("/api/v1/" + resource + "?pageNumber=-1&pageSize=10").session(session))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("pageNumber: must be 0 or greater"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"customers", "employees", "orders", "products", "suppliers"})
    void pageSizeZero_ShouldReturn400(String resource) throws Exception {
        mockMvc.perform(get("/api/v1/" + resource + "?pageNumber=0&pageSize=0").session(session))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("pageSize: must be at least 1"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"customers", "employees", "orders", "products", "suppliers"})
    void pageSizeAboveMaximum_ShouldReturn400(String resource) throws Exception {
        mockMvc.perform(get("/api/v1/" + resource + "?pageNumber=0&pageSize=101").session(session))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("pageSize: must be at most 100"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"customers", "employees", "orders", "products", "suppliers"})
    void pageSizeAtMaximum_ShouldReturn200(String resource) throws Exception {
        mockMvc.perform(get("/api/v1/" + resource + "?pageNumber=0&pageSize=100").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.size").value(100));
    }

    @ParameterizedTest
    @ValueSource(strings = {"customers", "employees", "orders", "products", "suppliers"})
    void frontendPageSize_ShouldReturn200(String resource) throws Exception {
        mockMvc.perform(get("/api/v1/" + resource + "?pageNumber=0&pageSize=50").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.size").value(50));
    }

    @ParameterizedTest
    @ValueSource(strings = {"orders"})
    void bothParametersInvalid_ShouldListBothErrors(String resource) throws Exception {
        mockMvc.perform(get("/api/v1/" + resource + "?pageNumber=-1&pageSize=0").session(session))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("pageNumber: must be 0 or greater; pageSize: must be at least 1"));
    }
}
