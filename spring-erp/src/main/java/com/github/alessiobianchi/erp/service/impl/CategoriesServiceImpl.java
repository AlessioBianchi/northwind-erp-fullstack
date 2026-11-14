package com.github.alessiobianchi.erp.service.impl;

import com.github.alessiobianchi.erp.converter.CategoryBeanToDtoConverter;
import com.github.alessiobianchi.erp.converter.CategoryDtoToBeanConverter;
import com.github.alessiobianchi.erp.dao.CategoriesDAO;
import com.github.alessiobianchi.erp.dao.ProductsDAO;
import com.github.alessiobianchi.erp.dto.CategoryDTO;
import com.github.alessiobianchi.erp.exception.ConflictException;
import com.github.alessiobianchi.erp.exception.ResourceNotFoundException;
import com.github.alessiobianchi.erp.model.Category;
import com.github.alessiobianchi.erp.model.CategoryBuilder;
import com.github.alessiobianchi.erp.service.CategoriesService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CategoriesServiceImpl implements CategoriesService {

    private final CategoriesDAO categoriesDao;
    private final ProductsDAO productsDao;

    public CategoriesServiceImpl(CategoriesDAO categoriesDao, ProductsDAO productsDao) {
        this.categoriesDao = categoriesDao;
        this.productsDao = productsDao;
    }

    @Override
    public List<CategoryDTO> findAllByOrderByCategoryIdDesc() {
        return categoriesDao.findAllByOrderByCategoryIdDesc().stream()
                .map(CategoryBeanToDtoConverter::convert)
                .toList();
    }

    @Override
    @Transactional
    public CategoryDTO create(CategoryDTO categoryDto) {
        return CategoryBeanToDtoConverter.convert(categoriesDao.save(CategoryDtoToBeanConverter.convert(categoryDto)));
    }

    @Override
    @Transactional
    public CategoryDTO update(int categoryId, CategoryDTO categoryDto) {
        Category existing = categoriesDao.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        Category categoryToUpdate = new CategoryBuilder(CategoryDtoToBeanConverter.convert(categoryDto))
                .withCategoryId(categoryId)
                .withCreatedAt(existing.getCreatedAt())
                .build();

        return CategoryBeanToDtoConverter.convert(categoriesDao.save(categoryToUpdate));
    }

    @Override
    @Transactional
    public void delete(int categoryId) {
        Category categoryToDelete = categoriesDao.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (!productsDao.findAllByCategory(categoryToDelete).isEmpty()) {
            throw new ConflictException("This category is connected to a product. It can't be deleted.");
        }

        categoriesDao.delete(categoryToDelete);
    }
}
