package com.github.alessiobianchi.erp.integration;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Request/response bodies of the resources that used to expose entities. Each test deletes the row it creates,
// so the seed-data counts in SearchIntegrationTest stay valid.
class DtoBodiesIntegrationTest extends AbstractIntegrationTest {

    private static final String OLD_AUDIT = ",\"createdAt\":\"2020-01-01T00:00:00Z\",\"updatedAt\":\"2020-01-01T00:00:00Z\"";

    private MockHttpSession session;

    @BeforeEach
    void setUp() throws Exception {
        session = login(EMPLOYEE, SEED_PASSWORD);
    }

    @ParameterizedTest(name = "{0}")
    @CsvSource(delimiter = '|', value = {
            "categories | categoryId | {\"categoryName\":\"DTO Cat\"",
            "customers  | customerId | {\"customerCode\":\"DTOT\",\"companyName\":\"DTO Customer\"",
            "products   | productId  | {\"productName\":\"DTO Product\",\"category\":{\"categoryId\":3},\"supplier\":{\"supplierId\":3}",
            "shippers   | shipperId  | {\"companyName\":\"DTO Shipper\"",
            "suppliers  | supplierId | {\"companyName\":\"DTO Supplier\""
    })
    void createAndUpdate_ShouldIgnoreAuditFieldsInBodyAndMoveUpdatedAt(String resource, String idField, String body) throws Exception {
        String base = "/api/v1/" + resource;
        String created = mockMvc.perform(post(base).session(session).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body + OLD_AUDIT + "}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$." + idField).value(notNullValue()))
                .andReturn().getResponse().getContentAsString();
        int id = JsonPath.read(created, "$." + idField);

        String stored = mockMvc.perform(get(base).session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]." + idField).value(id))
                .andReturn().getResponse().getContentAsString();
        String createdAt = JsonPath.read(stored, "$[0].createdAt");
        String updatedAtBefore = JsonPath.read(stored, "$[0].updatedAt");
        assertNotEquals("2020-01-01T00:00:00Z", createdAt);

        String updated = mockMvc.perform(put(base + "/" + id).session(session).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body + OLD_AUDIT + "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$." + idField).value(id))
                .andReturn().getResponse().getContentAsString();

        assertEquals(createdAt, JsonPath.read(updated, "$.createdAt"));
        assertNotEquals(updatedAtBefore, JsonPath.read(updated, "$.updatedAt"));

        mockMvc.perform(delete(base + "/" + id).session(session).with(csrf()))
                .andExpect(status().isNoContent());
    }

    @ParameterizedTest(name = "{0}")
    @CsvSource(delimiter = '|', value = {
            "categories | {\"categoryName\":\"\"}                                  | categoryName",
            "customers  | {\"customerCode\":\"TOOLONG\",\"companyName\":\"X\"}     | customerCode",
            "products   | {\"productName\":\"X\",\"unitPrice\":-1}                 | unitPrice",
            "shippers   | {}                                                       | companyName",
            "suppliers  | {}                                                       | companyName"
    })
    void create_WithInvalidBody_ShouldReturn400NamingTheField(String resource, String body, String field) throws Exception {
        mockMvc.perform(post("/api/v1/" + resource).session(session).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString(field)));
    }

    @Test
    void product_ShouldKeepNestedCategoryAndSupplierShape() throws Exception {
        mockMvc.perform(get("/api/v1/products?pageNumber=0&pageSize=1&search=chai").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].category.categoryName").value("Beverages"))
                .andExpect(jsonPath("$.content[0].supplier.companyName").value(notNullValue()))
                .andExpect(jsonPath("$.content[0].discontinued").value(true));
    }

    @Test
    void dashboard_LowStockProducts_ShouldBeProductDtos() throws Exception {
        mockMvc.perform(get("/api/v1/dashboard").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productStock[0].productId").value(notNullValue()))
                .andExpect(jsonPath("$.productStock[0].category.categoryName").value(notNullValue()));
    }
}
