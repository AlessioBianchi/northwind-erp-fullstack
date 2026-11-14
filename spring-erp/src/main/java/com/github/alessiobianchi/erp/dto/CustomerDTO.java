package com.github.alessiobianchi.erp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record CustomerDTO(
        Integer customerId,
        @NotBlank @Size(max = 5) String customerCode,
        @NotBlank @Size(max = 40) String companyName,
        @Size(max = 30) String contactName,
        @Size(max = 30) String contactTitle,
        @Size(max = 60) String address,
        @Size(max = 15) String city,
        @Size(max = 15) String region,
        @Size(max = 10) String postalCode,
        @Size(max = 15) String country,
        @Size(max = 24) String phone,
        @Size(max = 24) String fax,
        @JsonProperty(access = JsonProperty.Access.READ_ONLY) Instant createdAt,
        @JsonProperty(access = JsonProperty.Access.READ_ONLY) Instant updatedAt) {
}
