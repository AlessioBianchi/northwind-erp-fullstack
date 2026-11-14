package com.github.alessiobianchi.erp.converter;

import com.github.alessiobianchi.erp.dto.SupplierDTO;
import com.github.alessiobianchi.erp.model.Supplier;
import com.github.alessiobianchi.erp.model.SupplierBuilder;

public class SupplierDtoToBeanConverter {

    public static Supplier convert(SupplierDTO dto) {
        return new SupplierBuilder()
                .withSupplierId(dto.supplierId())
                .withCompanyName(dto.companyName())
                .withContactName(dto.contactName())
                .withContactTitle(dto.contactTitle())
                .withAddress(dto.address())
                .withCity(dto.city())
                .withRegion(dto.region())
                .withPostalCode(dto.postalCode())
                .withCountry(dto.country())
                .withPhone(dto.phone())
                .withFax(dto.fax())
                .withHomepage(dto.homepage())
                .build();
    }
}
