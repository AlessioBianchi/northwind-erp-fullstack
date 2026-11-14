package com.github.alessiobianchi.erp.converter;

import com.github.alessiobianchi.erp.dto.ProductDTO;
import com.github.alessiobianchi.erp.model.Product;
import com.github.alessiobianchi.erp.model.ProductBuilder;

public class ProductDtoToBeanConverter {

    public static Product convert(ProductDTO dto) {
        return new ProductBuilder()
                .withProductId(dto.productId())
                .withProductName(dto.productName())
                .withSupplier(dto.supplier() != null ? SupplierDtoToBeanConverter.convert(dto.supplier()) : null)
                .withCategory(dto.category() != null ? CategoryDtoToBeanConverter.convert(dto.category()) : null)
                .withQuantityPerUnit(dto.quantityPerUnit())
                .withUnitPrice(dto.unitPrice())
                .withUnitsInStock(dto.unitsInStock())
                .withUnitsOnOrder(dto.unitsOnOrder())
                .withReorderLevel(dto.reorderLevel())
                .withDiscontinued(Boolean.TRUE.equals(dto.discontinued()))
                .build();
    }
}
