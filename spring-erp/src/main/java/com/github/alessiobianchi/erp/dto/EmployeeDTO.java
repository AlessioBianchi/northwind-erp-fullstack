package com.github.alessiobianchi.erp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.time.LocalDate;

public record EmployeeDTO(
        Integer employeeId,
        @NotBlank @Size(max = 20) String lastName,
        @NotBlank @Size(max = 10) String firstName,
        @Size(max = 30) String title,
        @Size(max = 25) String titleOfCourtesy,
        LocalDate birthDate,
        LocalDate hireDate,
        @Size(max = 60) String address,
        @Size(max = 15) String city,
        @Size(max = 15) String region,
        @Size(max = 10) String postalCode,
        @Size(max = 15) String country,
        @Size(max = 24) String homePhone,
        @Size(max = 4) String extension,
        String notes,
        EmployeeDTO reportsTo,
        @Size(max = 50) String username,
        // BCrypt only uses the first 72 bytes of the raw password.
        @Size(max = 72) @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) String password,
        @JsonProperty(access = JsonProperty.Access.READ_ONLY) Instant createdAt,
        @JsonProperty(access = JsonProperty.Access.READ_ONLY) Instant updatedAt) {
}
