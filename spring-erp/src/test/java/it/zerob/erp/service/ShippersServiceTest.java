package it.zerob.erp.service;

import it.zerob.erp.dao.OrdersDAO;
import it.zerob.erp.dao.ShippersDAO;
import it.zerob.erp.model.Order;
import it.zerob.erp.model.Shipper;
import it.zerob.erp.model.ShipperBuilder;
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
    void save_ShouldReturnSavedShipper() {
        // Arrange
        when(shippersDao.save(any(Shipper.class))).thenReturn(sampleShipper);

        // Act
        Shipper saved = shippersService.save(new Shipper());

        // Assert
        assertNotNull(saved);
        assertEquals(1L, saved.getShipperId());
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