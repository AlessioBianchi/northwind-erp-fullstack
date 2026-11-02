package com.github.alessiobianchi.erp.service;

import com.github.alessiobianchi.erp.dao.OrdersDAO;
import com.github.alessiobianchi.erp.dao.ShippersDAO;
import com.github.alessiobianchi.erp.exception.ConflictException;
import com.github.alessiobianchi.erp.exception.ResourceNotFoundException;
import com.github.alessiobianchi.erp.model.Shipper;
import com.github.alessiobianchi.erp.model.ShipperBuilder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShippersService {

    private final ShippersDAO shippersDao;
    private final OrdersDAO ordersDao;

    public ShippersService(ShippersDAO shippersDao, OrdersDAO ordersDao) {
        this.shippersDao = shippersDao;
        this.ordersDao = ordersDao;
    }

    public List<Shipper> findAllByOrderByShipperIdDesc() {
        return shippersDao.findAllByOrderByShipperIdDesc();
    }

    public Shipper create(Shipper shipper) {
        return shippersDao.save(shipper);
    }

    public Shipper update(int shipperId, Shipper shipper) {
        if (!shippersDao.existsById(shipperId)) {
            throw new ResourceNotFoundException("Shipper not found");
        }

        Shipper shipperToUpdate = new ShipperBuilder(shipper)
                .withShipperId(shipperId)
                .build();

        return shippersDao.save(shipperToUpdate);
    }

    public void delete(int shipperId) {
        Shipper shipperToDelete = shippersDao.findById(shipperId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipper not found"));

        if (!ordersDao.findAllByShipper(shipperToDelete).isEmpty()) {
            throw new ConflictException("There are orders connected to this shipper. It can't be deleted.");
        }

        shippersDao.delete(shipperToDelete);
    }
}
