package com.github.alessiobianchi.erp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record ShipperDTO(
        Integer shipperId,
        @NotBlank @Size(max = 40) String companyName,
        @Size(max = 24) String phone,
        @JsonProperty(access = JsonProperty.Access.READ_ONLY) Instant createdAt,
        @JsonProperty(access = JsonProperty.Access.READ_ONLY) Instant updatedAt) {
}
