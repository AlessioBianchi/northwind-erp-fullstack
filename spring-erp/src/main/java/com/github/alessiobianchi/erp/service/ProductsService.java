package com.github.alessiobianchi.erp.service;

import com.github.alessiobianchi.erp.model.Product;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductsService {

    Page<Product> findAllByOrderByProductIdDesc(int pageNumber, int pageSize);

    List<Product> findAllByOrderByProductIdDesc();

    Product create(Product product);

    Product update(int productId, Product product);

    void delete(int productId);
}
