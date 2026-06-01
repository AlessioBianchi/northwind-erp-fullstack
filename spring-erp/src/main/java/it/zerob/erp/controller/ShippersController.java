package it.zerob.erp.controller;

import it.zerob.erp.model.Shipper;
import it.zerob.erp.service.ShippersService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/shippers")
public class ShippersController {

    private final ShippersService service;

    public ShippersController(ShippersService service) {
        this.service = service;
    }

    @GetMapping("/all")
    @ResponseBody
    public List<Shipper> findAllByOrderByShipperIdDesc() {
        return service.findAllByOrderByShipperIdDesc();
    }

    @PostMapping("/save")
    @ResponseBody
    public Shipper save(@RequestBody Shipper shipper) {
        return service.save(shipper);
    }

    @GetMapping("/delete/{shipperId}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long shipperId) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Supplier deleted!");

        if(!service.delete(shipperId)) {
            response.put("message", "There are orders connected to this shipper. It can't be deleted.");
        }

        return ResponseEntity.ok(response);
    }
}
