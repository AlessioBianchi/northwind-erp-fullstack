package com.github.alessiobianchi.erp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.Instant;

public record OrderDetailsDTO(
        @NotNull Integer orderId,
        @NotNull Integer productId,
        String productName,
        Double unitPrice,
        @NotNull @Positive Integer quantity,
        @NotNull @DecimalMin("0") @DecimalMax("1") Double discount,
        @JsonProperty(access = JsonProperty.Access.READ_ONLY) Instant createdAt,
        @JsonProperty(access = JsonProperty.Access.READ_ONLY) Instant updatedAt) {
}
