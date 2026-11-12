package com.github.alessiobianchi.erp.controller;

import com.github.alessiobianchi.erp.config.OpenApiConfig;
import com.github.alessiobianchi.erp.dto.OrderDTO;
import com.github.alessiobianchi.erp.dto.OrderDetailsDTO;
import com.github.alessiobianchi.erp.service.OrdersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Tag(name = "Orders", description = "Customer orders, their detail lines and PDF reports.")
@RestController
@RequestMapping("/api/v1/orders")
public class OrdersController {

    private final OrdersService service;

    public OrdersController(OrdersService service) {
        this.service = service;
    }

    @Operation(summary = "List orders",
            description = "Returns all orders, newest first." +
                    "\n\nWith `pageNumber` and `pageSize` it returns a page object instead of an array.")
    @ApiResponse(responseCode = "200", description = "Order list, or a page of orders when paginated")
    @GetMapping
    public List<OrderDTO> findAllByOrderByOrderIdDesc() {
        return service.findAllByOrderByOrderIdDesc();
    }

    @GetMapping(params = {"pageNumber", "pageSize"})
    public Page<OrderDTO> findAllByOrderByOrderIdDesc(
            @Parameter(description = "0-based page index; send together with pageSize") @RequestParam int pageNumber,
            @Parameter(description = "Page size; send together with pageNumber") @RequestParam int pageSize) {
        return service.findAllByOrderByOrderIdDesc(pageNumber, pageSize);
    }

    @Operation(summary = "Create an order",
            description = "Creates a new order. The employee is set to the logged-in user; " +
                    "any employee sent in the body is ignored.\n\n- 400 if `customerId` is missing")
    @ApiResponse(responseCode = "201", description = "Order created")
    @ApiResponse(responseCode = "400", ref = OpenApiConfig.BAD_REQUEST)
    @PostMapping()
    public ResponseEntity<OrderDTO> create(@Valid @RequestBody OrderDTO order, @Parameter(hidden = true) Principal principal) {
        OrderDTO orderSaved = service.create(order, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(orderSaved);
    }

    @Operation(summary = "Update an order", description = "Updates the order.\n\n- 404 if the order does not exist")
    @ApiResponse(responseCode = "200", description = "Order updated")
    @ApiResponse(responseCode = "400", ref = OpenApiConfig.BAD_REQUEST)
    @ApiResponse(responseCode = "404", ref = OpenApiConfig.NOT_FOUND)
    @PutMapping("/{orderId}")
    public ResponseEntity<OrderDTO> update(@PathVariable int orderId, @Valid @RequestBody OrderDTO order) {
        OrderDTO orderSaved = service.update(orderId, order);
        return ResponseEntity.ok(orderSaved);
    }

    @Operation(summary = "Delete an order",
            description = "Deletes the order.\n\n- 404 if the order does not exist\n" +
                    "- 409 if the order still has detail lines")
    @ApiResponse(responseCode = "204", description = "Order deleted")
    @ApiResponse(responseCode = "404", ref = OpenApiConfig.NOT_FOUND)
    @ApiResponse(responseCode = "409", ref = OpenApiConfig.CONFLICT)
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> delete(@PathVariable int orderId) {
        service.delete(orderId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "List the details of an order",
            description = "Returns the product lines of the order. Empty if the order has none or does not exist.")
    @ApiResponse(responseCode = "200", description = "Order detail lines")
    @GetMapping("/{orderId}/details")
    public List<OrderDetailsDTO> findAllOrderDetailsByOrderId(@PathVariable int orderId) {
        return service.findAllOrderDetailsByOrderId(orderId);
    }

    @Operation(summary = "Add a detail line to an order",
            description = "Adds a product line to the order. The unit price is copied from the product; " +
                    "any value sent is ignored.\n\n" +
                    "- 400 if the body's `orderId` does not match the path\n" +
                    "- 404 if the order or the product does not exist")
    @ApiResponse(responseCode = "201", description = "Order detail created")
    @ApiResponse(responseCode = "400", ref = OpenApiConfig.BAD_REQUEST)
    @ApiResponse(responseCode = "404", ref = OpenApiConfig.NOT_FOUND)
    @PostMapping("/{orderId}/details")
    public ResponseEntity<OrderDetailsDTO> create(@PathVariable int orderId, @Valid @RequestBody OrderDetailsDTO orderDetail) {
        OrderDetailsDTO orderDetailSaved = service.create(orderId, orderDetail);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderDetailSaved);
    }

    @Operation(summary = "Remove a detail line from an order",
            description = "Removes the line for that product from the order.\n\n" +
                    "- 404 if the order has no line for that product")
    @ApiResponse(responseCode = "204", description = "Order detail deleted")
    @ApiResponse(responseCode = "404", ref = OpenApiConfig.NOT_FOUND)
    @DeleteMapping("/{orderId}/details/{productId}")
    public ResponseEntity<Void> delete(@PathVariable int orderId, @PathVariable int productId) {
        service.delete(orderId, productId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Download the PDF report of an order",
            description = "Generates a PDF that summarizes the order and returns it as a download.\n\n" +
                    "- 404 if the order does not exist")
    @ApiResponse(responseCode = "200", description = "PDF report",
            content = @Content(mediaType = MediaType.APPLICATION_PDF_VALUE, schema = @Schema(type = "string", format = "binary")))
    @ApiResponse(responseCode = "404", ref = OpenApiConfig.NOT_FOUND)
    @GetMapping("/{id}/report")
    public ResponseEntity<byte[]> getOrdersReport(@PathVariable("id") int orderId) {
        byte[] pdfContent = service.getOrdersReport(orderId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);

        headers.setContentDispositionFormData("attachment", "order_report.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(pdfContent);
    }
}
