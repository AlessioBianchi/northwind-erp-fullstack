package com.github.alessiobianchi.erp.controller;

import com.github.alessiobianchi.erp.dto.OrderDetailsDTO;
import com.github.alessiobianchi.erp.exception.GlobalExceptionHandler;
import com.github.alessiobianchi.erp.service.OrdersService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OrdersControllerValidationTest {

    @Mock
    private OrdersService ordersService;

    @InjectMocks
    private OrdersController ordersController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(ordersController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createOrderDetail_WhenBodyIsInvalid_ShouldReturn400WithFieldErrors() throws Exception {
        String body = """
                { "orderId": 1, "quantity": 0, "discount": 1.5 }
                """;

        mockMvc.perform(post("/api/v1/orders/details")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("productId")))
                .andExpect(jsonPath("$.message", containsString("quantity")))
                .andExpect(jsonPath("$.message", containsString("discount")));

        verify(ordersService, never()).create(any(OrderDetailsDTO.class));
    }

    @Test
    void createOrderDetail_WhenBodyIsValid_ShouldCallService() throws Exception {
        String body = """
                { "orderId": 1, "productId": 50, "quantity": 2, "discount": 0.1 }
                """;
        when(ordersService.create(any(OrderDetailsDTO.class))).thenReturn(new OrderDetailsDTO());

        mockMvc.perform(post("/api/v1/orders/details")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());

        verify(ordersService).create(any(OrderDetailsDTO.class));
    }
}
