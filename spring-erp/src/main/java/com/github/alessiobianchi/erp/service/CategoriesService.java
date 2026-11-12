package com.github.alessiobianchi.erp.service;

import com.github.alessiobianchi.erp.model.Category;

import java.util.List;

public interface CategoriesService {

    List<Category> findAllByOrderByCategoryIdDesc();

    Category create(Category category);

    Category update(int categoryId, Category category);

    void delete(int categoryId);
}
