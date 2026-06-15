package com.github.alessiobianchi.erp.service;

import com.github.alessiobianchi.erp.dao.CategoriesDAO;
import com.github.alessiobianchi.erp.dao.ProductsDAO;
import com.github.alessiobianchi.erp.model.Category;
import com.github.alessiobianchi.erp.model.CategoryBuilder;
import com.github.alessiobianchi.erp.model.Product;
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

    public Category save(Category category) {
        return categoriesDao.save(category);
    }

    public boolean delete(Long categoryId) {
        Category categoryToDelete = new CategoryBuilder()
                .withCategoryId(categoryId)
                .build();

        List<Product> productList = productsDao.findAllByCategory(categoryToDelete);

        if (!productList.isEmpty()) {
            return false;
        }

        categoriesDao.delete(categoryToDelete);
        return true;
    }
}
