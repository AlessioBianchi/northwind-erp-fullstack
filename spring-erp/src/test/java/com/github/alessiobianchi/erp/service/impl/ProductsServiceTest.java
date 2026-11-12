package com.github.alessiobianchi.erp.service.impl;

import com.github.alessiobianchi.erp.dao.OrderDetailsDAO;
import com.github.alessiobianchi.erp.dao.ProductsDAO;
import com.github.alessiobianchi.erp.exception.ConflictException;
import com.github.alessiobianchi.erp.exception.ResourceNotFoundException;
import com.github.alessiobianchi.erp.model.OrderDetail;
import com.github.alessiobianchi.erp.model.Product;
import com.github.alessiobianchi.erp.model.ProductBuilder;
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
class ProductsServiceTest {

    @Mock
    private ProductsDAO productsDao;

    @Mock
    private OrderDetailsDAO orderDetailsDao;

    @InjectMocks
    private ProductsServiceImpl productsService;

    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        sampleProduct = new ProductBuilder()
                .withProductId(10)
                .withProductName("Chai")
                .withUnitPrice(18.00)
                .build();
    }

    @Test
    void findAllByOrderByProductIdDesc_WithPagination_ShouldReturnPage() {
        // Arrange
        int pageNum = 0;
        int pageSize = 5;
        Page<Product> productPage = new PageImpl<>(List.of(sampleProduct));
        when(productsDao.findAllByOrderByProductIdDesc(any(Pageable.class))).thenReturn(productPage);

        // Act
        Page<Product> result = productsService.findAllByOrderByProductIdDesc(pageNum, pageSize);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(productsDao).findAllByOrderByProductIdDesc(PageRequest.of(pageNum, pageSize));
    }

    @Test
    void findAllByOrderByProductIdDesc_ShouldReturnList() {
        // Arrange
        when(productsDao.findAllByOrderByProductIdDesc()).thenReturn(List.of(sampleProduct));

        // Act
        List<Product> result = productsService.findAllByOrderByProductIdDesc();

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void create_ShouldReturnCreatedProduct() {
        // Arrange
        when(productsDao.save(any(Product.class))).thenReturn(sampleProduct);

        // Act
        Product created = productsService.create(new Product());

        // Assert
        assertNotNull(created);
        assertEquals(10, created.getProductId());
        verify(productsDao).save(any(Product.class));
    }

    @Test
    void update_ShouldReturnUpdatedProduct() {
        // Arrange
        int productId = 1;
        String productName = "Test";
        Product productUpdated = new ProductBuilder()
                .withProductId(productId)
                .withProductName(productName)
                .build();
        when(productsDao.existsById(productId)).thenReturn(true);
        when(productsDao.save(any(Product.class))).thenReturn(productUpdated);

        // Act
        Product updated = productsService.update(productId, new Product());

        // Assert
        assertNotNull(updated);
        assertEquals(1, updated.getProductId());
        assertEquals("Test", updated.getProductName());
        verify(productsDao).save(any(Product.class));
    }

    @Test
    void update_WhenProductNotFound_ShouldThrowNotFound() {
        // Arrange
        when(productsDao.existsById(1)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> productsService.update(1, new Product()));
        verify(productsDao, never()).save(any(Product.class));
    }

    @Test
    void delete_WhenProductNotFound_ShouldThrowNotFound() {
        // Arrange
        when(productsDao.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> productsService.delete(1));
        verify(productsDao, never()).delete(any(Product.class));
    }

    @Test
    void delete_WhenProductIsInOrders_ShouldThrowConflict() {
        // Arrange
        Product product = new ProductBuilder().withProductId(1).build();
        when(productsDao.findById(1)).thenReturn(Optional.of(product));
        when(orderDetailsDao.findAllByProduct(product)).thenReturn(List.of(new OrderDetail()));

        // Act & Assert
        assertThrows(ConflictException.class, () -> productsService.delete(1));
        verify(productsDao, never()).delete(any(Product.class));
    }

    @Test
    void delete_WhenProductIsNotInOrders_ShouldDelete() {
        // Arrange
        Product product = new ProductBuilder().withProductId(1).build();
        when(productsDao.findById(1)).thenReturn(Optional.of(product));
        when(orderDetailsDao.findAllByProduct(product)).thenReturn(List.of());

        // Act
        productsService.delete(1);

        // Assert
        verify(productsDao).delete(product);
    }
}
