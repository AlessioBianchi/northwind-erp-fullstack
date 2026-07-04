package com.github.alessiobianchi.erp.dao;

import com.github.alessiobianchi.erp.model.Category;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CategoriesDAO extends CrudRepository<Category, Integer> {

    List<Category> findAllByOrderByCategoryIdDesc();
}
