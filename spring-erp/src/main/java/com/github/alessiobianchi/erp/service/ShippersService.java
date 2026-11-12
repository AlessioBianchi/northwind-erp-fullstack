package com.github.alessiobianchi.erp.service;

import com.github.alessiobianchi.erp.model.Shipper;

import java.util.List;

public interface ShippersService {

    List<Shipper> findAllByOrderByShipperIdDesc();

    Shipper create(Shipper shipper);

    Shipper update(int shipperId, Shipper shipper);

    void delete(int shipperId);
}
