package com.github.alessiobianchi.erp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.time.Instant;

// discontinued is Boolean so a body without it binds; null is stored as false.
public record ProductDTO(
        Integer productId,
        @NotBlank @Size(max = 40) String productName,
        SupplierDTO supplier,
        CategoryDTO category,
        @Size(max = 20) String quantityPerUnit,
        @PositiveOrZero Double unitPrice,
        @PositiveOrZero Integer unitsInStock,
        @PositiveOrZero Integer unitsOnOrder,
        @PositiveOrZero Integer reorderLevel,
        Boolean discontinued,
        @JsonProperty(access = JsonProperty.Access.READ_ONLY) Instant createdAt,
        @JsonProperty(access = JsonProperty.Access.READ_ONLY) Instant updatedAt) {
}
