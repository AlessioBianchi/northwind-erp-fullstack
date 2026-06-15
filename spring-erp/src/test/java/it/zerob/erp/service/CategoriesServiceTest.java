package it.zerob.erp.service;

import it.zerob.erp.dao.CategoriesDAO;
import it.zerob.erp.dao.ProductsDAO;
import it.zerob.erp.model.Category;
import it.zerob.erp.model.CategoryBuilder;
import it.zerob.erp.model.Product;
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
class CategoriesServiceTest {

    @Mock
    private CategoriesDAO categoriesDao;

    @Mock
    private ProductsDAO productsDao;

    @InjectMocks
    private CategoriesService categoriesService;

    private Category sampleCategory;

    @BeforeEach
    void setUp() {
        sampleCategory = new CategoryBuilder()
                .withCategoryId(1L)
                .build();
    }

    @Test
    void findAllByOrderByCategoryIdDesc_ShouldReturnList() {
        // Arrange
        when(categoriesDao.findAllByOrderByCategoryIdDesc()).thenReturn(List.of(sampleCategory));

        // Act
        List<Category> result = categoriesService.findAllByOrderByCategoryIdDesc();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(categoriesDao, times(1)).findAllByOrderByCategoryIdDesc();
    }

    @Test
    void save_ShouldReturnSavedCategory() {
        // Arrange
        when(categoriesDao.save(any(Category.class))).thenReturn(sampleCategory);

        // Act
        Category saved = categoriesService.save(new Category());

        // Assert
        assertEquals(1L, saved.getCategoryId());
        verify(categoriesDao, times(1)).save(any(Category.class));
    }

    @Test
    void delete_WhenCategoryHasProducts_ShouldReturnFalseAndNotDelete() {
        // Arrange
        Long categoryId = 1L;
        when(productsDao.findAllByCategory(any(Category.class))).thenReturn(List.of(new Product()));

        // Act
        boolean result = categoriesService.delete(categoryId);

        // Assert
        assertFalse(result);
        verify(categoriesDao, never()).delete(any(Category.class));
    }

    @Test
    void delete_WhenCategoryHasNoProducts_ShouldReturnTrueAndDelete() {
        // Arrange
        Long categoryId = 1L;
        when(productsDao.findAllByCategory(any(Category.class))).thenReturn(Collections.emptyList());

        // Act
        boolean result = categoriesService.delete(categoryId);

        // Assert
        assertTrue(result);
        verify(categoriesDao, times(1)).delete(any(Category.class));
    }
}