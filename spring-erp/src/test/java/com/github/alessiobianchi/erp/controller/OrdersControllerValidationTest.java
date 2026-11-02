package com.github.alessiobianchi.erp.controller;

import com.github.alessiobianchi.erp.dto.OrderDTO;
import com.github.alessiobianchi.erp.dto.OrderDetailsDTO;
import com.github.alessiobianchi.erp.exception.BadRequestException;
import com.github.alessiobianchi.erp.exception.ConflictException;
import com.github.alessiobianchi.erp.exception.GlobalExceptionHandler;
import com.github.alessiobianchi.erp.exception.ResourceNotFoundException;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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

        mockMvc.perform(post("/api/v1/orders/1/details")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("productId")))
                .andExpect(jsonPath("$.message", containsString("quantity")))
                .andExpect(jsonPath("$.message", containsString("discount")));

        verify(ordersService, never()).create(anyInt(), any(OrderDetailsDTO.class));
    }

    @Test
    void createOrderDetail_WhenBodyIsValid_ShouldReturn201() throws Exception {
        String body = """
                { "orderId": 1, "productId": 50, "quantity": 2, "discount": 0.1 }
                """;
        when(ordersService.create(eq(1), any(OrderDetailsDTO.class))).thenReturn(new OrderDetailsDTO());

        mockMvc.perform(post("/api/v1/orders/1/details")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        verify(ordersService).create(eq(1), any(OrderDetailsDTO.class));
    }

    @Test
    void createOrderDetail_WhenOrderIdMismatch_ShouldReturn400() throws Exception {
        String body = """
                { "orderId": 7, "productId": 50, "quantity": 2, "discount": 0.1 }
                """;
        when(ordersService.create(eq(5), any(OrderDetailsDTO.class)))
                .thenThrow(new BadRequestException("orderId in body does not match the path."));

        mockMvc.perform(post("/api/v1/orders/5/details")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void createOrder_WhenCustomerIdMissing_ShouldReturn400() throws Exception {
        String body = """
                { "freight": 10.0 }
                """;

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("customerId")));

        verify(ordersService, never()).create(any(OrderDTO.class), anyString());
    }

    @Test
    void deleteOrder_WhenDeleted_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/v1/orders/1"))
                .andExpect(status().isNoContent());

        verify(ordersService).delete(1);
    }

    @Test
    void deleteOrder_WhenHasDetails_ShouldReturn409() throws Exception {
        doThrow(new ConflictException("There are details on this order. It can't be deleted."))
                .when(ordersService).delete(1);

        mockMvc.perform(delete("/api/v1/orders/1"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("There are details on this order. It can't be deleted."));
    }

    @Test
    void deleteOrder_WhenNotFound_ShouldReturn404() throws Exception {
        doThrow(new ResourceNotFoundException("Order not found"))
                .when(ordersService).delete(99);

        mockMvc.perform(delete("/api/v1/orders/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void deleteOrderDetail_WhenDeleted_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/v1/orders/1/details/50"))
                .andExpect(status().isNoContent());

        verify(ordersService).delete(1, 50);
    }
}
