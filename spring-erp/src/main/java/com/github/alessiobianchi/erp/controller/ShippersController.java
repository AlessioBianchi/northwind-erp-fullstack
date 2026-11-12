package com.github.alessiobianchi.erp.controller;

import com.github.alessiobianchi.erp.config.OpenApiConfig;
import com.github.alessiobianchi.erp.model.Shipper;
import com.github.alessiobianchi.erp.service.ShippersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Shippers", description = "Shipping companies that deliver the orders.")
@RestController
@RequestMapping("/api/v1/shippers")
public class ShippersController {

    private final ShippersService service;

    public ShippersController(ShippersService service) {
        this.service = service;
    }

    @Operation(summary = "List shippers", description = "Returns all shippers, newest first.")
    @ApiResponse(responseCode = "200", description = "Shipper list")
    @GetMapping
    public List<Shipper> findAllByOrderByShipperIdDesc() {
        return service.findAllByOrderByShipperIdDesc();
    }

    @Operation(summary = "Create a shipper", description = "Creates a new shipper.")
    @ApiResponse(responseCode = "201", description = "Shipper created")
    @ApiResponse(responseCode = "400", ref = OpenApiConfig.BAD_REQUEST)
    @PostMapping()
    public ResponseEntity<Shipper> create(@Valid @RequestBody Shipper shipper) {
        Shipper shipperSaved = service.create(shipper);
        return ResponseEntity.status(HttpStatus.CREATED).body(shipperSaved);
    }

    @Operation(summary = "Update a shipper", description = "Updates the shipper.\n\n- 404 if the shipper does not exist")
    @ApiResponse(responseCode = "200", description = "Shipper updated")
    @ApiResponse(responseCode = "400", ref = OpenApiConfig.BAD_REQUEST)
    @ApiResponse(responseCode = "404", ref = OpenApiConfig.NOT_FOUND)
    @PutMapping("/{shipperId}")
    public ResponseEntity<Shipper> update(@PathVariable int shipperId, @Valid @RequestBody Shipper shipper) {
        Shipper shipperSaved = service.update(shipperId, shipper);
        return ResponseEntity.ok(shipperSaved);
    }

    @Operation(summary = "Delete a shipper",
            description = "Deletes the shipper.\n\n- 404 if the shipper does not exist\n" +
                    "- 409 if the shipper has orders")
    @ApiResponse(responseCode = "204", description = "Shipper deleted")
    @ApiResponse(responseCode = "404", ref = OpenApiConfig.NOT_FOUND)
    @ApiResponse(responseCode = "409", ref = OpenApiConfig.CONFLICT)
    @DeleteMapping("/{shipperId}")
    public ResponseEntity<Void> delete(@PathVariable int shipperId) {
        service.delete(shipperId);
        return ResponseEntity.noContent().build();
    }
}
