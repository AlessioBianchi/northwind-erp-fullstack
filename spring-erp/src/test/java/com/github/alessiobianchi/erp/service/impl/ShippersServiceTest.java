package com.github.alessiobianchi.erp.service.impl;

import com.github.alessiobianchi.erp.dao.OrdersDAO;
import com.github.alessiobianchi.erp.dao.ShippersDAO;
import com.github.alessiobianchi.erp.exception.ConflictException;
import com.github.alessiobianchi.erp.exception.ResourceNotFoundException;
import com.github.alessiobianchi.erp.model.Order;
import com.github.alessiobianchi.erp.model.Shipper;
import com.github.alessiobianchi.erp.model.ShipperBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

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
    private ShippersServiceImpl shippersService;

    private Shipper sampleShipper;

    @BeforeEach
    void setUp() {
        sampleShipper = new ShipperBuilder()
                .withShipperId(1)
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
        assertEquals(1, created.getShipperId());
        verify(shippersDao).save(any(Shipper.class));
    }

    @Test
    void update_ShouldReturnUpdatedShipper() {
        // Arrange
        Shipper shipperUpdated = new ShipperBuilder()
                .withShipperId(1)
                .withCompanyName("Test")
                .build();
        Instant createdAt = Instant.parse("2020-01-01T00:00:00Z");
        when(shippersDao.findById(1)).thenReturn(Optional.of(new ShipperBuilder()
                .withShipperId(1)
                .withCreatedAt(createdAt)
                .build()));
        when(shippersDao.save(any(Shipper.class))).thenReturn(shipperUpdated);

        // Act
        Shipper updated = shippersService.update(1, new Shipper());

        // Assert
        assertNotNull(updated);
        assertEquals(1, updated.getShipperId());
        assertEquals("Test", updated.getCompanyName());
        verify(shippersDao).save(any(Shipper.class));
        verify(shippersDao).save(argThat(saved -> createdAt.equals(saved.getCreatedAt())));
    }

    @Test
    void update_WhenShipperNotFound_ShouldThrowNotFound() {
        // Arrange
        when(shippersDao.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> shippersService.update(1, new Shipper()));
        verify(shippersDao, never()).save(any(Shipper.class));
    }

    @Test
    void delete_WhenShipperNotFound_ShouldThrowNotFound() {
        // Arrange
        when(shippersDao.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> shippersService.delete(1));
        verify(shippersDao, never()).delete(any(Shipper.class));
    }

    @Test
    void delete_WhenShipperHasOrders_ShouldThrowConflict() {
        // Arrange
        Shipper shipper = new ShipperBuilder().withShipperId(1).build();
        when(shippersDao.findById(1)).thenReturn(Optional.of(shipper));
        when(ordersDao.findAllByShipper(shipper)).thenReturn(List.of(new Order()));

        // Act & Assert
        assertThrows(ConflictException.class, () -> shippersService.delete(1));
        verify(shippersDao, never()).delete(any(Shipper.class));
    }

    @Test
    void delete_WhenShipperHasNoOrders_ShouldDelete() {
        // Arrange
        Shipper shipper = new ShipperBuilder().withShipperId(1).build();
        when(shippersDao.findById(1)).thenReturn(Optional.of(shipper));
        when(ordersDao.findAllByShipper(shipper)).thenReturn(List.of());

        // Act
        shippersService.delete(1);

        // Assert
        verify(shippersDao).delete(shipper);
    }
}
