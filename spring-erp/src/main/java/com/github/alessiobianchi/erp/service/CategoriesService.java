package com.github.alessiobianchi.erp.service;

import com.github.alessiobianchi.erp.dto.CategoryDTO;

import java.util.List;

public interface CategoriesService {

    List<CategoryDTO> findAllByOrderByCategoryIdDesc();

    CategoryDTO create(CategoryDTO categoryDto);

    CategoryDTO update(int categoryId, CategoryDTO categoryDto);

    void delete(int categoryId);
}
