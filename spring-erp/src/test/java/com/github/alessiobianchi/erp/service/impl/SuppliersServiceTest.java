package com.github.alessiobianchi.erp.service.impl;

import com.github.alessiobianchi.erp.dao.ProductsDAO;
import com.github.alessiobianchi.erp.dao.SuppliersDAO;
import com.github.alessiobianchi.erp.exception.ConflictException;
import com.github.alessiobianchi.erp.exception.ResourceNotFoundException;
import com.github.alessiobianchi.erp.model.Product;
import com.github.alessiobianchi.erp.model.Supplier;
import com.github.alessiobianchi.erp.model.SupplierBuilder;
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

import java.util.List;
import java.util.Optional;

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
    private SuppliersServiceImpl suppliersService;

    private Supplier sampleSupplier;

    @BeforeEach
    void setUp() {
        sampleSupplier = new SupplierBuilder()
                .withSupplierId(1)
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
        assertEquals(1, created.getSupplierId());
        verify(suppliersDao).save(any(Supplier.class));
    }

    @Test
    void update_ShouldReturnUpdatedSupplier() {
        // Arrange
        Supplier supplierUpdated = new SupplierBuilder()
                .withSupplierId(1)
                .withCompanyName("Test")
                .build();
        when(suppliersDao.existsById(1)).thenReturn(true);
        when(suppliersDao.save(any(Supplier.class))).thenReturn(supplierUpdated);

        // Act
        Supplier updated = suppliersService.update(1, new Supplier());

        // Assert
        assertNotNull(updated);
        assertEquals(1, updated.getSupplierId());
        assertEquals("Test", updated.getCompanyName());
        verify(suppliersDao).save(any(Supplier.class));
    }

    @Test
    void update_WhenSupplierNotFound_ShouldThrowNotFound() {
        // Arrange
        when(suppliersDao.existsById(1)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> suppliersService.update(1, new Supplier()));
        verify(suppliersDao, never()).save(any(Supplier.class));
    }

    @Test
    void delete_WhenSupplierNotFound_ShouldThrowNotFound() {
        // Arrange
        when(suppliersDao.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> suppliersService.delete(1));
        verify(suppliersDao, never()).delete(any(Supplier.class));
    }

    @Test
    void delete_WhenSupplierHasProducts_ShouldThrowConflict() {
        // Arrange
        Supplier supplier = new SupplierBuilder().withSupplierId(1).build();
        when(suppliersDao.findById(1)).thenReturn(Optional.of(supplier));
        when(productsDao.findAllBySupplier(supplier)).thenReturn(List.of(new Product()));

        // Act & Assert
        assertThrows(ConflictException.class, () -> suppliersService.delete(1));
        verify(suppliersDao, never()).delete(any(Supplier.class));
    }

    @Test
    void delete_WhenSupplierHasNoProducts_ShouldDelete() {
        // Arrange
        Supplier supplier = new SupplierBuilder().withSupplierId(1).build();
        when(suppliersDao.findById(1)).thenReturn(Optional.of(supplier));
        when(productsDao.findAllBySupplier(supplier)).thenReturn(List.of());

        // Act
        suppliersService.delete(1);

        // Assert
        verify(suppliersDao).delete(supplier);
    }
}
