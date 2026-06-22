package it.zerob.erp.service;

import it.zerob.erp.dao.ProductsDAO;
import it.zerob.erp.dao.SuppliersDAO;
import it.zerob.erp.model.Product;
import it.zerob.erp.model.Supplier;
import it.zerob.erp.model.SupplierBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SuppliersServiceTest {

    @Mock
    private SuppliersDAO suppliersDao;

    @Mock
    private ProductsDAO productsDao;

    @InjectMocks
    private SuppliersService suppliersService;

    private Supplier sampleSupplier;

    @BeforeEach
    void setUp() {
        sampleSupplier = new SupplierBuilder()
                .withSupplierId(1L)
                .withCompanyName("Test")
                .build();
    }

    @Test
    void findAllByOrderBySupplierIdDesc_ShouldReturnList() {
        // Arrange
        when(suppliersDao.findAllByOrderBySupplierIdDesc()).thenReturn(List.of(sampleSupplier));

        // Act
        List<Supplier> result = suppliersService.findAllByOrderBySupplierIdDesc();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(suppliersDao).findAllByOrderBySupplierIdDesc();
    }

    @Test
    void findAllByOrderBySupplierIdDesc_WithPagination_ShouldReturnPage() {
        // Arrange
        int page = 0;
        int size = 10;
        Page<Supplier> supplierPage = new PageImpl<>(List.of(sampleSupplier));
        when(suppliersDao.findAllByOrderBySupplierIdDesc(any(Pageable.class))).thenReturn(supplierPage);

        // Act
        Page<Supplier> result = suppliersService.findAllByOrderBySupplierIdDesc(page, size);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(suppliersDao).findAllByOrderBySupplierIdDesc(PageRequest.of(page, size));
    }

    @Test
    void create_ShouldReturnCreatedSupplier() {
        // Arrange
        when(suppliersDao.save(any(Supplier.class))).thenReturn(sampleSupplier);

        // Act
        Supplier created = suppliersService.create(new Supplier());

        // Assert
        assertNotNull(created);
        assertEquals(1L, created.getSupplierId());
        verify(suppliersDao).save(any(Supplier.class));
    }

    @Test
    void update_ShouldReturnUpdatedSupplier() {
        // Arrange
        Supplier supplierUpdated = new SupplierBuilder()
                .withSupplierId(1L)
                .withCompanyName("Test")
                .build();
        when(suppliersDao.save(any(Supplier.class))).thenReturn(supplierUpdated);

        // Act
        Supplier updated = suppliersService.update(1L, new Supplier());

        // Assert
        assertNotNull(updated);
        assertEquals(1L, updated.getSupplierId());
        assertEquals("Test", updated.getCompanyName());
        verify(suppliersDao).save(any(Supplier.class));
    }

    @Test
    void delete_WhenSupplierHasProducts_ShouldReturnFalse() {
        // Arrange
        Long supplierId = 1L;
        when(productsDao.findAllBySupplier(any(Supplier.class))).thenReturn(List.of(new Product()));

        // Act
        boolean result = suppliersService.delete(supplierId);

        // Assert
        assertFalse(result);
        verify(suppliersDao, never()).delete(any(Supplier.class));
    }

    @Test
    void delete_WhenSupplierHasNoProducts_ShouldReturnTrue() {
        // Arrange
        Long supplierId = 1L;
        when(productsDao.findAllBySupplier(any(Supplier.class))).thenReturn(Collections.emptyList());

        // Act
        boolean result = suppliersService.delete(supplierId);

        // Assert
        assertTrue(result);
        verify(suppliersDao, times(1)).delete(any(Supplier.class));
    }
}