package com.github.alessiobianchi.erp.model;

public class CategoryBuilder {

    private int categoryId;
    private String categoryName;
    private String description;

    public CategoryBuilder() {
    }

    public CategoryBuilder(Category category) {
        this.categoryId = category.getCategoryId();
        this.categoryName = category.getCategoryName();
        this.description = category.getDescription();
    }

    public CategoryBuilder withCategoryId(int categoryId) {
        this.categoryId = categoryId;
        return this;
    }

    public CategoryBuilder withCategoryName(String categoryName) {
        this.categoryName = categoryName;
        return this;
    }

    public CategoryBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public Category build() {
        return new Category(
                categoryId,
                categoryName,
                description
        );
    }
}