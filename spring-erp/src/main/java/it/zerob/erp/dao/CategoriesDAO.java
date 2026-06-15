package it.zerob.erp.dao;

import it.zerob.erp.model.Category;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CategoriesDAO extends CrudRepository<Category, Long> {

    List<Category> findAllByOrderByCategoryIdDesc();
}
