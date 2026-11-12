package com.github.alessiobianchi.erp.controller;

import com.github.alessiobianchi.erp.dto.DashboardDTO;
import com.github.alessiobianchi.erp.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Dashboard", description = "Aggregated statistics for the home page.")
@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    private final DashboardService service;

    public DashboardController(DashboardService service) {
        this.service = service;
    }

    @Operation(summary = "Get dashboard statistics",
            description = "Returns the dashboard statistics:\n\n" +
                    "- total number of orders and total revenue\n" +
                    "- number of orders and revenue for the current month\n" +
                    "- the 10 most recent orders\n" +
                    "- number of orders per product category\n" +
                    "- products with fewer than 10 units in stock")
    @ApiResponse(responseCode = "200", description = "Dashboard statistics")
    @GetMapping()
    public ResponseEntity<DashboardDTO> getStats() {
        return ResponseEntity.ok(service.getStats());
    }
}
