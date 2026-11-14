package com.github.alessiobianchi.erp.converter;

import com.github.alessiobianchi.erp.dto.CategoryDTO;
import com.github.alessiobianchi.erp.model.Category;

public class CategoryBeanToDtoConverter {

    public static CategoryDTO convert(Category bean) {
        return new CategoryDTO(
                bean.getCategoryId(),
                bean.getCategoryName(),
                bean.getDescription(),
                bean.getCreatedAt(),
                bean.getUpdatedAt());
    }
}
