package com.github.alessiobianchi.erp.converter;

import com.github.alessiobianchi.erp.dto.ProductDTO;
import com.github.alessiobianchi.erp.model.Product;

public class ProductBeanToDtoConverter {

    public static ProductDTO convert(Product bean) {
        return new ProductDTO(
                bean.getProductId(),
                bean.getProductName(),
                bean.getSupplier() != null ? SupplierBeanToDtoConverter.convert(bean.getSupplier()) : null,
                bean.getCategory() != null ? CategoryBeanToDtoConverter.convert(bean.getCategory()) : null,
                bean.getQuantityPerUnit(),
                bean.getUnitPrice(),
                bean.getUnitsInStock(),
                bean.getUnitsOnOrder(),
                bean.getReorderLevel(),
                bean.getDiscontinued(),
                bean.getCreatedAt(),
                bean.getUpdatedAt());
    }
}
