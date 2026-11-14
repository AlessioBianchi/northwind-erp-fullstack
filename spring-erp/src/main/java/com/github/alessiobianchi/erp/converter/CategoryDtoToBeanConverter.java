package com.github.alessiobianchi.erp.converter;

import com.github.alessiobianchi.erp.dto.CategoryDTO;
import com.github.alessiobianchi.erp.model.Category;
import com.github.alessiobianchi.erp.model.CategoryBuilder;

public class CategoryDtoToBeanConverter {

    public static Category convert(CategoryDTO dto) {
        return new CategoryBuilder()
                .withCategoryId(dto.categoryId())
                .withCategoryName(dto.categoryName())
                .withDescription(dto.description())
                .build();
    }
}
