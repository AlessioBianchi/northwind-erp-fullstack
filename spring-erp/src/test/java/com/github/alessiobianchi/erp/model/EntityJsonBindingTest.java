package com.github.alessiobianchi.erp.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import tools.jackson.databind.json.JsonMapper;

import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

// A create request has no ID yet; Jackson binds entities through their constructor and rejects a missing primitive
// (400 "Malformed request body"), so IDs are Integer and Product.discontinued is a Boolean parameter.
class EntityJsonBindingTest {

    private static final JsonMapper MAPPER = JsonMapper.builder().build();

    static Stream<Arguments> createBodiesWithoutId() {
        return Stream.of(
                Arguments.of(Category.class, "{\"categoryName\":\"Tea\"}", (Function<Category, Object>) Category::getCategoryId),
                Arguments.of(Customer.class, "{\"companyName\":\"ACME\"}", (Function<Customer, Object>) Customer::getCustomerId),
                Arguments.of(Employee.class, "{\"firstName\":\"John\",\"lastName\":\"Doe\"}", (Function<Employee, Object>) Employee::getEmployeeId),
                Arguments.of(Product.class, "{\"productName\":\"Chai\"}", (Function<Product, Object>) Product::getProductId),
                Arguments.of(Shipper.class, "{\"companyName\":\"Speedy\"}", (Function<Shipper, Object>) Shipper::getShipperId),
                Arguments.of(Supplier.class, "{\"companyName\":\"Exotic Liquids\"}", (Function<Supplier, Object>) Supplier::getSupplierId)
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("createBodiesWithoutId")
    <T> void deserialize_WhenIdMissing_ShouldLeaveIdNull(Class<T> type, String json, Function<T, Object> idGetter) {
        T entity = MAPPER.readValue(json, type);

        assertNull(idGetter.apply(entity));
    }

    @Test
    void deserializeProduct_WhenDiscontinuedMissing_ShouldDefaultToFalse() {
        Product product = MAPPER.readValue("{\"productName\":\"Chai\"}", Product.class);

        assertFalse(product.getDiscontinued());
    }
}
