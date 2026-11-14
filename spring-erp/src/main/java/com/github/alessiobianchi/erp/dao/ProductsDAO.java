package com.github.alessiobianchi.erp.dao;

import com.github.alessiobianchi.erp.model.Category;
import com.github.alessiobianchi.erp.model.Order;
import com.github.alessiobianchi.erp.model.Product;
import com.github.alessiobianchi.erp.model.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductsDAO extends CrudRepository<Product, Integer> {

    Page<Product> findAllByOrderByProductIdDesc(Pageable pageable);

    List<Product> findAllByOrderByProductIdDesc();

    List<Product> findAllByCategory(Category category);

    List<Product> findAllBySupplier(Supplier supplier);

    List<Product> findByUnitsInStockLessThan(Integer threshold);

    @Query("""
            select p from Product p left join p.category c left join p.supplier s
            where lower(p.productName) like lower(concat('%', :search, '%'))
               or lower(c.categoryName) like lower(concat('%', :search, '%'))
               or lower(s.companyName) like lower(concat('%', :search, '%'))
            order by p.productId desc""")
    Page<Product> search(@Param("search") String search, Pageable pageable);
}
