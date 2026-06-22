package it.zerob.erp.service;

import it.zerob.erp.dao.OrderDetailsDAO;
import it.zerob.erp.dao.ProductsDAO;
import it.zerob.erp.model.OrderDetail;
import it.zerob.erp.model.Product;
import it.zerob.erp.model.ProductBuilder;
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
class ProductsServiceTest {

    @Mock
    private ProductsDAO productsDao;

    @Mock
    private OrderDetailsDAO orderDetailsDao;

    @InjectMocks
    private ProductsService productsService;

    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        sampleProduct = new ProductBuilder()
                .withProductId(10L)
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
        assertEquals(10L, created.getProductId());
        verify(productsDao).save(any(Product.class));
    }

    @Test
    void update_ShouldReturnUpdatedProduct() {
        // Arrange
        Long productId = 1L;
        String productName = "Test";
        Product productUpdated = new ProductBuilder()
                .withProductId(productId)
                .withProductName(productName)
                .build();
        when(productsDao.save(any(Product.class))).thenReturn(productUpdated);

        // Act
        Product updated = productsService.update(productId, new Product());

        // Assert
        assertNotNull(updated);
        assertEquals(1L, updated.getProductId());
        assertEquals("Test", updated.getProductName());
        verify(productsDao).save(any(Product.class));
    }

    @Test
    void delete_WhenProductIsInOrders_ShouldReturnFalse() {
        // Arrange
        Long productId = 10L;
        when(orderDetailsDao.findAllByProduct(any(Product.class))).thenReturn(List.of(new OrderDetail()));

        // Act
        boolean result = productsService.delete(productId);

        // Assert
        assertFalse(result);
        verify(productsDao, never()).delete(any(Product.class));
    }

    @Test
    void delete_WhenProductIsNotInOrders_ShouldReturnTrue() {
        // Arrange
        Long productId = 10L;
        when(orderDetailsDao.findAllByProduct(any(Product.class))).thenReturn(Collections.emptyList());

        // Act
        boolean result = productsService.delete(productId);

        // Assert
        assertTrue(result);
        verify(productsDao, times(1)).delete(any(Product.class));
    }
}