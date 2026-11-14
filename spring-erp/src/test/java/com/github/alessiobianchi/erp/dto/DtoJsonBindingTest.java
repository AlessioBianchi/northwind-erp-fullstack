package com.github.alessiobianchi.erp.dto;

import com.github.alessiobianchi.erp.converter.ProductDtoToBeanConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import tools.jackson.databind.json.JsonMapper;

import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

// A create request has no ID yet; Jackson rejects a missing primitive (400 "Malformed request body"),
// so request DTO IDs are Integer and ProductDTO.discontinued is a Boolean.
class DtoJsonBindingTest {

    private static final JsonMapper MAPPER = JsonMapper.builder().build();

    static Stream<Arguments> createBodiesWithoutId() {
        return Stream.of(
                Arguments.of(CategoryDTO.class, "{\"categoryName\":\"Tea\"}", (Function<CategoryDTO, Object>) CategoryDTO::categoryId),
                Arguments.of(CustomerDTO.class, "{\"companyName\":\"ACME\"}", (Function<CustomerDTO, Object>) CustomerDTO::customerId),
                Arguments.of(EmployeeDTO.class, "{\"firstName\":\"John\",\"lastName\":\"Doe\"}", (Function<EmployeeDTO, Object>) EmployeeDTO::getEmployeeId),
                Arguments.of(ProductDTO.class, "{\"productName\":\"Chai\"}", (Function<ProductDTO, Object>) ProductDTO::productId),
                Arguments.of(ShipperDTO.class, "{\"companyName\":\"Speedy\"}", (Function<ShipperDTO, Object>) ShipperDTO::shipperId),
                Arguments.of(SupplierDTO.class, "{\"companyName\":\"Exotic Liquids\"}", (Function<SupplierDTO, Object>) SupplierDTO::supplierId)
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("createBodiesWithoutId")
    <T> void deserialize_WhenIdMissing_ShouldLeaveIdNull(Class<T> type, String json, Function<T, Object> idGetter) {
        T dto = MAPPER.readValue(json, type);

        assertNull(idGetter.apply(dto));
    }

    @Test
    void deserializeProduct_WhenDiscontinuedMissing_ShouldStoreFalse() {
        ProductDTO dto = MAPPER.readValue("{\"productName\":\"Chai\"}", ProductDTO.class);

        assertFalse(ProductDtoToBeanConverter.convert(dto).getDiscontinued());
    }

    @Test
    void deserializeProduct_ShouldKeepNestedCategoryAndSupplier() {
        ProductDTO dto = MAPPER.readValue(
                "{\"productName\":\"Chai\",\"category\":{\"categoryId\":1},\"supplier\":{\"supplierId\":8}}", ProductDTO.class);

        assertEquals(1, dto.category().categoryId());
        assertEquals(8, dto.supplier().supplierId());
    }

    @Test
    void deserialize_ShouldIgnoreReadOnlyAuditFields() {
        CategoryDTO dto = MAPPER.readValue(
                "{\"categoryName\":\"Tea\",\"createdAt\":\"2020-01-01T00:00:00Z\",\"updatedAt\":\"2020-01-01T00:00:00Z\"}",
                CategoryDTO.class);

        assertNull(dto.createdAt());
        assertNull(dto.updatedAt());
    }
}
