package com.github.alessiobianchi.erp.service;

import com.github.alessiobianchi.erp.dao.OrdersDAO;
import com.github.alessiobianchi.erp.dao.ShippersDAO;
import com.github.alessiobianchi.erp.model.Order;
import com.github.alessiobianchi.erp.model.Shipper;
import com.github.alessiobianchi.erp.model.ShipperBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShippersServiceTest {

    @Mock
    private ShippersDAO shippersDao;

    @Mock
    private OrdersDAO ordersDao;

    @InjectMocks
    private ShippersService shippersService;

    private Shipper sampleShipper;

    @BeforeEach
    void setUp() {
        sampleShipper = new ShipperBuilder()
                .withShipperId(1L)
                .withCompanyName("Speedy Express")
                .withPhone("0123456789")
                .build();
    }

    @Test
    void findAllByOrderByShipperIdDesc_ShouldReturnList() {
        // Arrange
        when(shippersDao.findAllByOrderByShipperIdDesc()).thenReturn(List.of(sampleShipper));

        // Act
        List<Shipper> result = shippersService.findAllByOrderByShipperIdDesc();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Speedy Express", result.get(0).getCompanyName());
        verify(shippersDao, times(1)).findAllByOrderByShipperIdDesc();
    }

    @Test
    void create_ShouldReturnCreatedShipper() {
        // Arrange
        when(shippersDao.save(any(Shipper.class))).thenReturn(sampleShipper);

        // Act
        Shipper created = shippersService.create(new Shipper());

        // Assert
        assertNotNull(created);
        assertEquals(1L, created.getShipperId());
        verify(shippersDao).save(any(Shipper.class));
    }

    @Test
    void update_ShouldReturnUpdatedShipper() {
        // Arrange
        Shipper shipperUpdated = new ShipperBuilder()
                .withShipperId(1L)
                .withCompanyName("Test")
                .build();
        when(shippersDao.save(any(Shipper.class))).thenReturn(shipperUpdated);

        // Act
        Shipper updated = shippersService.update(1L, new Shipper());

        // Assert
        assertNotNull(updated);
        assertEquals(1L, updated.getShipperId());
        assertEquals("Test", updated.getCompanyName());
        verify(shippersDao).save(any(Shipper.class));
    }

    @Test
    void delete_WhenShipperHasOrders_ShouldReturnFalse() {
        // Arrange
        Long shipperId = 1L;
        when(ordersDao.findAllByShipper(any(Shipper.class))).thenReturn(List.of(new Order()));

        // Act
        boolean result = shippersService.delete(shipperId);

        // Assert
        assertFalse(result);
        verify(shippersDao, never()).delete(any(Shipper.class));
    }

    @Test
    void delete_WhenShipperHasNoOrders_ShouldReturnTrue() {
        // Arrange
        Long shipperId = 1L;
        when(ordersDao.findAllByShipper(any(Shipper.class))).thenReturn(Collections.emptyList());

        // Act
        boolean result = shippersService.delete(shipperId);

        // Assert
        assertTrue(result);
        verify(shippersDao, times(1)).delete(any(Shipper.class));
    }
}