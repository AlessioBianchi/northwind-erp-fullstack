package it.zerob.erp.dao;

import it.zerob.erp.model.Shipper;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ShippersDAO extends CrudRepository<Shipper, Long> {

    List<Shipper> findAllByOrderByShipperIdDesc();
}
