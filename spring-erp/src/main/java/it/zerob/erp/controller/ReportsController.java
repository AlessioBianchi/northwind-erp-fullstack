package it.zerob.erp.controller;

import it.zerob.erp.service.ReportsService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/reports")
public class ReportsController {

    private final ReportsService service;

    public ReportsController(ReportsService service) {
        this.service = service;
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<byte[]> getOrdersReport(@PathVariable("id") Long orderId) {
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
