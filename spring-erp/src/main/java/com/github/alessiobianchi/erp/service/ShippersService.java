package com.github.alessiobianchi.erp.service;

import com.github.alessiobianchi.erp.dto.ShipperDTO;

import java.util.List;

public interface ShippersService {

    List<ShipperDTO> findAllByOrderByShipperIdDesc();

    ShipperDTO create(ShipperDTO shipperDto);

    ShipperDTO update(int shipperId, ShipperDTO shipperDto);

    void delete(int shipperId);
}
