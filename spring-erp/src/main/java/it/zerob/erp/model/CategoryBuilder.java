package it.zerob.erp.model;

public class CategoryBuilder {

    private Long categoryId;
    private String categoryName;
    private String description;
    private String picture;

    public CategoryBuilder() {
    }

    public CategoryBuilder(Category category) {
        this.categoryId = category.getCategoryId();
        this.categoryName = category.getCategoryName();
        this.description = category.getDescription();
        this.picture = category.getPicture();
    }

    public CategoryBuilder withCategoryId(Long categoryId) {
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

    public CategoryBuilder withPicture(String picture) {
        this.picture = picture;
        return this;
    }

    public Category build() {
        return new Category(
                categoryId,
                categoryName,
                description,
                picture
        );
    }
}