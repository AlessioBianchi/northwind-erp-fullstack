package com.github.alessiobianchi.erp.model;

import java.time.Instant;

public class CategoryBuilder {

    private Integer categoryId;
    private String categoryName;
    private String description;
    private Instant createdAt;

    public CategoryBuilder() {
    }

    public CategoryBuilder(Category category) {
        this.categoryId = category.getCategoryId();
        this.categoryName = category.getCategoryName();
        this.description = category.getDescription();
        this.createdAt = category.getCreatedAt();
    }

    public CategoryBuilder withCategoryId(Integer categoryId) {
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

    public CategoryBuilder withCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Category build() {
        Category category = new Category(
                categoryId,
                categoryName,
                description
        );
        category.setCreatedAt(createdAt);
        return category;
    }
}