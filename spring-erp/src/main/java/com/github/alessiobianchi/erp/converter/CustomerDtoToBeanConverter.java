package com.github.alessiobianchi.erp.converter;

import com.github.alessiobianchi.erp.dto.CustomerDTO;
import com.github.alessiobianchi.erp.model.Customer;
import com.github.alessiobianchi.erp.model.CustomerBuilder;

public class CustomerDtoToBeanConverter {

    public static Customer convert(CustomerDTO dto) {
        return new CustomerBuilder()
                .withCustomerId(dto.customerId())
                .withCustomerCode(dto.customerCode())
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
                .build();
    }
}
