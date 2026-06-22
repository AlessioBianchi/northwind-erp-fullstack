package it.zerob.erp.service;

import it.zerob.erp.dao.CategoriesDAO;
import it.zerob.erp.dao.ProductsDAO;
import it.zerob.erp.model.Category;
import it.zerob.erp.model.CategoryBuilder;
import it.zerob.erp.model.Product;
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

    public Category update(Long categoryId, Category category) {
        Category categoryToUpdate = new CategoryBuilder(category)
                .withCategoryId(categoryId)
                .build();

        return categoriesDao.save(categoryToUpdate);
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
