package com.github.alessiobianchi.erp.integration;

import com.github.alessiobianchi.erp.dao.EmployeesDAO;
import com.github.alessiobianchi.erp.dao.ProductsDAO;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class OrdersIntegrationTest extends AbstractIntegrationTest {

    private static final int CUSTOMER_ID = 1;
    private static final int PRODUCT_ID = 1;

    @Autowired
    private EmployeesDAO employeesDao;

    @Autowired
    private ProductsDAO productsDao;

    private MockHttpSession session;

    @BeforeEach
    void setUp() throws Exception {
        session = login(EMPLOYEE, SEED_PASSWORD);
    }

    @Test
    void createOrder_ShouldSetEmployeeFromLoggedInUserAndAuditFields() throws Exception {
        int loggedInEmployeeId = employeesDao.findByUsername(EMPLOYEE).orElseThrow().getEmployeeId();
        int otherEmployeeId = employeesDao.findByUsername(MANAGER).orElseThrow().getEmployeeId();

        mockMvc.perform(post("/api/v1/orders").session(session).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customerId\":" + CUSTOMER_ID + ",\"employeeId\":" + otherEmployeeId + ",\"freight\":12.5}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value(notNullValue()))
                .andExpect(jsonPath("$.employeeId").value(loggedInEmployeeId))
                .andExpect(jsonPath("$.customerId").value(CUSTOMER_ID))
                .andExpect(jsonPath("$.createdAt").value(notNullValue()))
                .andExpect(jsonPath("$.updatedAt").value(notNullValue()));
    }

    @Test
    void createOrder_WithoutCustomer_ShouldReturn400() throws Exception {
        mockMvc.perform(post("/api/v1/orders").session(session).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"freight\":12.5}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("customerId")));
    }

    @Test
    void updateOrder_ShouldKeepCreatedAtAndMoveUpdatedAt() throws Exception {
        int orderId = createOrder();
        String created = mockMvc.perform(get("/api/v1/orders?pageNumber=0&pageSize=1").session(session))
                .andReturn().getResponse().getContentAsString();
        String createdAt = JsonPath.read(created, "$.content[0].createdAt");
        String updatedAtBefore = JsonPath.read(created, "$.content[0].updatedAt");

        String updated = mockMvc.perform(put("/api/v1/orders/" + orderId).session(session).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orderId\":" + orderId + ",\"customerId\":" + CUSTOMER_ID + ",\"freight\":99.0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.freight").value(99.0))
                .andReturn().getResponse().getContentAsString();

        assertEquals(createdAt, JsonPath.read(updated, "$.createdAt"));
        assertNotEquals(updatedAtBefore, JsonPath.read(updated, "$.updatedAt"));
    }

    @Test
    void addDetail_ShouldCopyUnitPriceFromProduct() throws Exception {
        int orderId = createOrder();
        double productPrice = productsDao.findById(PRODUCT_ID).orElseThrow().getUnitPrice();

        mockMvc.perform(post("/api/v1/orders/" + orderId + "/details").session(session).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(detailJson(orderId, "\"unitPrice\":0.01")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.unitPrice").value(productPrice))
                .andExpect(jsonPath("$.productName").value(notNullValue()));

        mockMvc.perform(get("/api/v1/orders/" + orderId + "/details").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].productId").value(PRODUCT_ID));
    }

    @Test
    void addDetail_WhenOrderDoesNotExist_ShouldReturn404() throws Exception {
        mockMvc.perform(post("/api/v1/orders/999999/details").session(session).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(detailJson(999999, null)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Order not found"));
    }

    @Test
    void addDetail_WhenBodyOrderIdDiffersFromPath_ShouldReturn400() throws Exception {
        int orderId = createOrder();

        mockMvc.perform(post("/api/v1/orders/" + orderId + "/details").session(session).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(detailJson(orderId + 1, null)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("orderId in body does not match the path."));
    }

    @Test
    void deleteOrder_WithDetails_ShouldReturn409UntilDetailsAreRemoved() throws Exception {
        int orderId = createOrder();
        mockMvc.perform(post("/api/v1/orders/" + orderId + "/details").session(session).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(detailJson(orderId, null)))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/api/v1/orders/" + orderId).session(session).with(csrf()))
                .andExpect(status().isConflict());

        mockMvc.perform(delete("/api/v1/orders/" + orderId + "/details/" + PRODUCT_ID).session(session).with(csrf()))
                .andExpect(status().isNoContent());
        mockMvc.perform(delete("/api/v1/orders/" + orderId).session(session).with(csrf()))
                .andExpect(status().isNoContent());
        mockMvc.perform(delete("/api/v1/orders/" + orderId).session(session).with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    void report_ShouldReturnPdf() throws Exception {
        int orderId = createOrder();

        byte[] pdf = mockMvc.perform(get("/api/v1/orders/" + orderId + "/report").session(session))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andReturn().getResponse().getContentAsByteArray();

        assertEquals("%PDF", new String(pdf, 0, 4));
    }

    @Test
    void report_WhenOrderDoesNotExist_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/api/v1/orders/999999/report").session(session))
                .andExpect(status().isNotFound());
    }

    @Test
    void listOrders_Paginated_ShouldReturnRequestedPageSize() throws Exception {
        mockMvc.perform(get("/api/v1/orders?pageNumber=0&pageSize=5").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(5))
                .andExpect(jsonPath("$.page.size").value(5));
    }

    private int createOrder() throws Exception {
        String body = mockMvc.perform(post("/api/v1/orders").session(session).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customerId\":" + CUSTOMER_ID + ",\"freight\":1.0}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(body, "$.orderId");
    }

    private static String detailJson(int orderId, String extraField) {
        return "{\"orderId\":" + orderId + ",\"productId\":" + PRODUCT_ID + ",\"quantity\":2,\"discount\":0"
                + (extraField != null ? "," + extraField : "") + "}";
    }
}
