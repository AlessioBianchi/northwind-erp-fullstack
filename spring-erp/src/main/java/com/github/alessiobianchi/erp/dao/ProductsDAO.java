package com.github.alessiobianchi.erp.dao;

import com.github.alessiobianchi.erp.model.Category;
import com.github.alessiobianchi.erp.model.Order;
import com.github.alessiobianchi.erp.model.Product;
import com.github.alessiobianchi.erp.model.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProductsDAO extends CrudRepository<Product, Long> {

    Page<Product> findAllByOrderByProductIdDesc(Pageable pageable);

    List<Product> findAllByOrderByProductIdDesc();

    List<Product> findAllByCategory(Category category);

    List<Product> findAllBySupplier(Supplier supplier);

    List<Product> findByUnitsInStockLessThan(Integer threshold);
}
