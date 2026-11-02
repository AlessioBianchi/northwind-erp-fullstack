package com.github.alessiobianchi.erp.controller;

import com.github.alessiobianchi.erp.model.Shipper;
import com.github.alessiobianchi.erp.service.ShippersService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/shippers")
public class ShippersController {

    private final ShippersService service;

    public ShippersController(ShippersService service) {
        this.service = service;
    }

    @GetMapping
    public List<Shipper> findAllByOrderByShipperIdDesc() {
        return service.findAllByOrderByShipperIdDesc();
    }

    @PostMapping()
    public ResponseEntity<Shipper> create(@Valid @RequestBody Shipper shipper) {
        Shipper shipperSaved = service.create(shipper);
        return ResponseEntity.status(HttpStatus.CREATED).body(shipperSaved);
    }

    @PutMapping("/{shipperId}")
    public ResponseEntity<Shipper> update(@PathVariable int shipperId, @Valid @RequestBody Shipper shipper) {
        Shipper shipperSaved = service.update(shipperId, shipper);
        return ResponseEntity.ok(shipperSaved);
    }

    @DeleteMapping("/{shipperId}")
    public ResponseEntity<Void> delete(@PathVariable int shipperId) {
        service.delete(shipperId);
        return ResponseEntity.noContent().build();
    }
}
