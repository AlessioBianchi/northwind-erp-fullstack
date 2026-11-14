package com.github.alessiobianchi.erp.converter;

import com.github.alessiobianchi.erp.dto.CustomerDTO;
import com.github.alessiobianchi.erp.model.Customer;

public class CustomerBeanToDtoConverter {

    public static CustomerDTO convert(Customer bean) {
        return new CustomerDTO(
                bean.getCustomerId(),
                bean.getCustomerCode(),
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
                bean.getCreatedAt(),
                bean.getUpdatedAt());
    }
}
