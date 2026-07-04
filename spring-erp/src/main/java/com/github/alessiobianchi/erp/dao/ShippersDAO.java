package com.github.alessiobianchi.erp.dao;

import com.github.alessiobianchi.erp.model.Shipper;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ShippersDAO extends CrudRepository<Shipper, Integer> {

    List<Shipper> findAllByOrderByShipperIdDesc();
}
