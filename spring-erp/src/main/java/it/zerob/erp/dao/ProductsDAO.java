package it.zerob.erp.dao;

import it.zerob.erp.model.Category;
import it.zerob.erp.model.Order;
import it.zerob.erp.model.Product;
import it.zerob.erp.model.Supplier;
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
