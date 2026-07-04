package com.github.alessiobianchi.erp.model;

import jakarta.persistence.*;

@Entity
@Table(name="CATEGORIES")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int categoryId;

    private String categoryName;
    private String description;

    public Category() {}

    public Category(int categoryId,
                    String categoryName,
                    String description) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.description = description;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getDescription() {
        return description;
    }
}
