package com.github.alessiobianchi.erp.service.impl;

import com.github.alessiobianchi.erp.dao.CategoriesDAO;
import com.github.alessiobianchi.erp.dao.ProductsDAO;
import com.github.alessiobianchi.erp.exception.ConflictException;
import com.github.alessiobianchi.erp.exception.ResourceNotFoundException;
import com.github.alessiobianchi.erp.model.Category;
import com.github.alessiobianchi.erp.model.CategoryBuilder;
import com.github.alessiobianchi.erp.service.CategoriesService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriesServiceImpl implements CategoriesService {

    private final CategoriesDAO categoriesDao;
    private final ProductsDAO productsDao;

    public CategoriesServiceImpl(CategoriesDAO categoriesDao, ProductsDAO productsDao) {
        this.categoriesDao = categoriesDao;
        this.productsDao = productsDao;
    }

    @Override
    public List<Category> findAllByOrderByCategoryIdDesc() {
        return categoriesDao.findAllByOrderByCategoryIdDesc();
    }

    @Override
    public Category create(Category category) {
        return categoriesDao.save(category);
    }

    @Override
    public Category update(int categoryId, Category category) {
        Category existing = categoriesDao.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        Category categoryToUpdate = new CategoryBuilder(category)
                .withCategoryId(categoryId)
                .withCreatedAt(existing.getCreatedAt())
                .build();

        return categoriesDao.save(categoryToUpdate);
    }

    @Override
    public void delete(int categoryId) {
        Category categoryToDelete = categoriesDao.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (!productsDao.findAllByCategory(categoryToDelete).isEmpty()) {
            throw new ConflictException("This category is connected to a product. It can't be deleted.");
        }

        categoriesDao.delete(categoryToDelete);
    }
}
