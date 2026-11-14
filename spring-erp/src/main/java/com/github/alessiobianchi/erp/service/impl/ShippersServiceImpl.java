package com.github.alessiobianchi.erp.service.impl;

import com.github.alessiobianchi.erp.dao.OrdersDAO;
import com.github.alessiobianchi.erp.dao.ShippersDAO;
import com.github.alessiobianchi.erp.exception.ConflictException;
import com.github.alessiobianchi.erp.exception.ResourceNotFoundException;
import com.github.alessiobianchi.erp.model.Shipper;
import com.github.alessiobianchi.erp.model.ShipperBuilder;
import com.github.alessiobianchi.erp.service.ShippersService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ShippersServiceImpl implements ShippersService {

    private final ShippersDAO shippersDao;
    private final OrdersDAO ordersDao;

    public ShippersServiceImpl(ShippersDAO shippersDao, OrdersDAO ordersDao) {
        this.shippersDao = shippersDao;
        this.ordersDao = ordersDao;
    }

    @Override
    public List<Shipper> findAllByOrderByShipperIdDesc() {
        return shippersDao.findAllByOrderByShipperIdDesc();
    }

    @Override
    @Transactional
    public Shipper create(Shipper shipper) {
        return shippersDao.save(shipper);
    }

    @Override
    @Transactional
    public Shipper update(int shipperId, Shipper shipper) {
        Shipper existing = shippersDao.findById(shipperId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipper not found"));

        Shipper shipperToUpdate = new ShipperBuilder(shipper)
                .withShipperId(shipperId)
                .withCreatedAt(existing.getCreatedAt())
                .build();

        return shippersDao.save(shipperToUpdate);
    }

    @Override
    @Transactional
    public void delete(int shipperId) {
        Shipper shipperToDelete = shippersDao.findById(shipperId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipper not found"));

        if (!ordersDao.findAllByShipper(shipperToDelete).isEmpty()) {
            throw new ConflictException("There are orders connected to this shipper. It can't be deleted.");
        }

        shippersDao.delete(shipperToDelete);
    }
}
