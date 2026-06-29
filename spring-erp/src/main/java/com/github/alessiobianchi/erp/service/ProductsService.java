package com.github.alessiobianchi.erp.service;

import com.github.alessiobianchi.erp.dao.OrderDetailsDAO;
import com.github.alessiobianchi.erp.dao.ProductsDAO;
import com.github.alessiobianchi.erp.model.Product;
import com.github.alessiobianchi.erp.model.ProductBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductsService {

    private final ProductsDAO productsDao;
    private final OrderDetailsDAO orderDetailsDao;

    public ProductsService(ProductsDAO productsDao, OrderDetailsDAO orderDetailsDao) {
        this.productsDao = productsDao;
        this.orderDetailsDao = orderDetailsDao;
    }

    public Page<Product> findAllByOrderByProductIdDesc(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return productsDao.findAllByOrderByProductIdDesc(pageable);
    }

    public List<Product> findAllByOrderByProductIdDesc() {
        return productsDao.findAllByOrderByProductIdDesc();
    }

    public Product create(Product product) {
        return productsDao.save(product);
    }

    public Product update(Long productId, Product product) {
        Product productToUpdate = new ProductBuilder(product)
                .withProductId(productId)
                .build();

        return productsDao.save(productToUpdate);
    }

    public boolean delete(Long productId) {
        Product productToDelete = new ProductBuilder()
                .withProductId(productId)
                .build();

        if (!orderDetailsDao.findAllByProduct(productToDelete).isEmpty()) {
            return false;
        }

        productsDao.delete(productToDelete);
        return true;
    }
}
