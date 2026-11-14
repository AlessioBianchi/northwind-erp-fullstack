package com.github.alessiobianchi.erp.converter;

import com.github.alessiobianchi.erp.dto.ShipperDTO;
import com.github.alessiobianchi.erp.model.Shipper;

public class ShipperBeanToDtoConverter {

    public static ShipperDTO convert(Shipper bean) {
        return new ShipperDTO(
                bean.getShipperId(),
                bean.getCompanyName(),
                bean.getPhone(),
                bean.getCreatedAt(),
                bean.getUpdatedAt());
    }
}
