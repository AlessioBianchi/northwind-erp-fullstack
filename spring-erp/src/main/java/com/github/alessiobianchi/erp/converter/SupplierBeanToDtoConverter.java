package com.github.alessiobianchi.erp.converter;

import com.github.alessiobianchi.erp.dto.SupplierDTO;
import com.github.alessiobianchi.erp.model.Supplier;

public class SupplierBeanToDtoConverter {

    public static SupplierDTO convert(Supplier bean) {
        return new SupplierDTO(
                bean.getSupplierId(),
                bean.getCompanyName(),
                bean.getContactName(),
                bean.getContactTitle(),
                bean.getAddress(),
                bean.getCity(),
                bean.getRegion(),
                bean.getPostalCode(),
                bean.getCountry(),
                bean.getPhone(),
                bean.getFax(),
                bean.getHomepage(),
                bean.getCreatedAt(),
                bean.getUpdatedAt());
    }
}
