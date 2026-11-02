package com.github.alessiobianchi.erp.service;

import com.github.alessiobianchi.erp.dao.CategoriesDAO;
import com.github.alessiobianchi.erp.dao.ProductsDAO;
import com.github.alessiobianchi.erp.exception.ConflictException;
import com.github.alessiobianchi.erp.exception.ResourceNotFoundException;
import com.github.alessiobianchi.erp.model.Category;
import com.github.alessiobianchi.erp.model.CategoryBuilder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriesService {

    private final CategoriesDAO categoriesDao;
    private final ProductsDAO productsDao;

    public CategoriesService(CategoriesDAO categoriesDao, ProductsDAO productsDao) {
        this.categoriesDao = categoriesDao;
        this.productsDao = productsDao;
    }

    public List<Category> findAllByOrderByCategoryIdDesc() {
        return categoriesDao.findAllByOrderByCategoryIdDesc();
    }

    public Category create(Category category) {
        return categoriesDao.save(category);
    }

    public Category update(int categoryId, Category category) {
        if (!categoriesDao.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category not found");
        }

        Category categoryToUpdate = new CategoryBuilder(category)
                .withCategoryId(categoryId)
                .build();

        return categoriesDao.save(categoryToUpdate);
    }

    public void delete(int categoryId) {
        Category categoryToDelete = categoriesDao.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (!productsDao.findAllByCategory(categoryToDelete).isEmpty()) {
            throw new ConflictException("This category is connected to a product. It can't be deleted.");
        }

        categoriesDao.delete(categoryToDelete);
    }
}
