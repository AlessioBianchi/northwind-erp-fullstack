package com.github.alessiobianchi.erp.controller;

import com.github.alessiobianchi.erp.dto.DashboardDTO;
import com.github.alessiobianchi.erp.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    private final DashboardService service;

    public DashboardController(DashboardService service) {
        this.service = service;
    }

    @GetMapping("/stats")
    public ResponseEntity<DashboardDTO> getStats() {
        return ResponseEntity.ok(service.getStats());
    }
}
