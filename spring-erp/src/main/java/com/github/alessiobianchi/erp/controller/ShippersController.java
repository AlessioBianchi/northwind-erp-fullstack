package com.github.alessiobianchi.erp.controller;

import com.github.alessiobianchi.erp.model.Shipper;
import com.github.alessiobianchi.erp.service.ShippersService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/v1/shippers")
public class ShippersController {

    private final ShippersService service;

    public ShippersController(ShippersService service) {
        this.service = service;
    }

    @GetMapping
    @ResponseBody
    public List<Shipper> findAllByOrderByShipperIdDesc() {
        return service.findAllByOrderByShipperIdDesc();
    }

    @PostMapping()
    @ResponseBody
    public ResponseEntity<Shipper> create(@RequestBody Shipper shipper) {
        Shipper shipperSaved = service.create(shipper);
        return ResponseEntity.ok(shipperSaved);
    }

    @PutMapping("/{shipperId}")
    @ResponseBody
    public ResponseEntity<Shipper> update(@PathVariable int shipperId, @RequestBody Shipper shipper) {
        Shipper shipperSaved = service.update(shipperId, shipper);
        return ResponseEntity.ok(shipperSaved);
    }

    @DeleteMapping("/delete/{shipperId}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> delete(@PathVariable int shipperId) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Shipper deleted!");

        if(!service.delete(shipperId)) {
            response.put("message", "There are orders connected to this shipper. It can't be deleted.");
        }

        return ResponseEntity.ok(response);
    }
}
