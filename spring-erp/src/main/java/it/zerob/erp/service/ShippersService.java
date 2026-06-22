package it.zerob.erp.service;

import it.zerob.erp.dao.OrdersDAO;
import it.zerob.erp.dao.ShippersDAO;
import it.zerob.erp.model.Shipper;
import it.zerob.erp.model.ShipperBuilder;
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

    public Shipper update(Long shipperId, Shipper shipper) {
        Shipper shipperToUpdate = new ShipperBuilder(shipper)
                .withShipperId(shipperId)
                .build();

        return shippersDao.save(shipperToUpdate);
    }

    public boolean delete(Long shipperId) {
        Shipper shipperToDelete = new ShipperBuilder()
                .withShipperId(shipperId)
                .build();

        if (!ordersDao.findAllByShipper(shipperToDelete).isEmpty()) {
            return false;
        }

        shippersDao.delete(shipperToDelete);
        return true;
    }
}
