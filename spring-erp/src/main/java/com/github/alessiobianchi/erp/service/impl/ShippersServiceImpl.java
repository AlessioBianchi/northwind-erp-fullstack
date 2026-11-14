package com.github.alessiobianchi.erp.service.impl;

import com.github.alessiobianchi.erp.converter.ShipperBeanToDtoConverter;
import com.github.alessiobianchi.erp.converter.ShipperDtoToBeanConverter;
import com.github.alessiobianchi.erp.dao.OrdersDAO;
import com.github.alessiobianchi.erp.dao.ShippersDAO;
import com.github.alessiobianchi.erp.dto.ShipperDTO;
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
    public List<ShipperDTO> findAllByOrderByShipperIdDesc() {
        return shippersDao.findAllByOrderByShipperIdDesc().stream()
                .map(ShipperBeanToDtoConverter::convert)
                .toList();
    }

    @Override
    @Transactional
    public ShipperDTO create(ShipperDTO shipperDto) {
        return ShipperBeanToDtoConverter.convert(shippersDao.save(ShipperDtoToBeanConverter.convert(shipperDto)));
    }

    @Override
    @Transactional
    public ShipperDTO update(int shipperId, ShipperDTO shipperDto) {
        Shipper existing = shippersDao.findById(shipperId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipper not found"));

        Shipper shipperToUpdate = new ShipperBuilder(ShipperDtoToBeanConverter.convert(shipperDto))
                .withShipperId(shipperId)
                .withCreatedAt(existing.getCreatedAt())
                .build();

        return ShipperBeanToDtoConverter.convert(shippersDao.save(shipperToUpdate));
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
