package com.github.alessiobianchi.erp.model;

import jakarta.persistence.*;

@Entity
@Table(name="CATEGORIES")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_NW_CATEGORIES")
    @SequenceGenerator(
            name = "SEQ_NW_CATEGORIES",
            sequenceName = "SEQ_NW_CATEGORIES",
            allocationSize = 1
    )
    private Long categoryId;

    private String categoryName;
    private String description;
    private String picture;

    public Category() {}

    public Category(Long categoryId,
                    String categoryName,
                    String description,
                    String picture) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.description = description;
        this.picture = picture;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getDescription() {
        return description;
    }

    public String getPicture() {
        return picture;
    }
}
