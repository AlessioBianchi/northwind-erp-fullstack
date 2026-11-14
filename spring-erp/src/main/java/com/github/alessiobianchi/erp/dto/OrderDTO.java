package com.github.alessiobianchi.erp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.time.LocalDate;

public record OrderDTO(
        Integer orderId,
        @NotNull Integer customerId,
        String customerName,
        Integer employeeId,
        String employeeName,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd") LocalDate orderDate,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd") LocalDate requiredDate,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd") LocalDate shippedDate,
        Integer shipperId,
        String shipperName,
        @PositiveOrZero Double freight,
        @Size(max = 40) String shipName,
        @Size(max = 60) String shipAddress,
        @Size(max = 15) String shipCity,
        @Size(max = 15) String shipRegion,
        @Size(max = 10) String shipPostalCode,
        @Size(max = 15) String shipCountry,
        @JsonProperty(access = JsonProperty.Access.READ_ONLY) Instant createdAt,
        @JsonProperty(access = JsonProperty.Access.READ_ONLY) Instant updatedAt) {
}
