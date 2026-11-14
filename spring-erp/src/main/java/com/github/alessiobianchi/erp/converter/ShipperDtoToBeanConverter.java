package com.github.alessiobianchi.erp.converter;

import com.github.alessiobianchi.erp.dto.ShipperDTO;
import com.github.alessiobianchi.erp.model.Shipper;
import com.github.alessiobianchi.erp.model.ShipperBuilder;

public class ShipperDtoToBeanConverter {

    public static Shipper convert(ShipperDTO dto) {
        return new ShipperBuilder()
                .withShipperId(dto.shipperId())
                .withCompanyName(dto.companyName())
                .withPhone(dto.phone())
                .build();
    }
}
