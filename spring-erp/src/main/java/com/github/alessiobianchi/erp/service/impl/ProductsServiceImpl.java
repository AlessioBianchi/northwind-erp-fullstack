package com.github.alessiobianchi.erp.service.impl;

import com.github.alessiobianchi.erp.dao.OrderDetailsDAO;
import com.github.alessiobianchi.erp.dao.ProductsDAO;
import com.github.alessiobianchi.erp.exception.ConflictException;
import com.github.alessiobianchi.erp.exception.ResourceNotFoundException;
import com.github.alessiobianchi.erp.model.Product;
import com.github.alessiobianchi.erp.model.ProductBuilder;
import com.github.alessiobianchi.erp.service.ProductsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class ProductsServiceImpl implements ProductsService {

    private final ProductsDAO productsDao;
    private final OrderDetailsDAO orderDetailsDao;

    public ProductsServiceImpl(ProductsDAO productsDao, OrderDetailsDAO orderDetailsDao) {
        this.productsDao = productsDao;
        this.orderDetailsDao = orderDetailsDao;
    }

    @Override
    public Page<Product> findAllByOrderByProductIdDesc(int pageNumber, int pageSize, String search) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return StringUtils.hasText(search)
                ? productsDao.search(search.trim(), pageable)
                : productsDao.findAllByOrderByProductIdDesc(pageable);
    }

    @Override
    public List<Product> findAllByOrderByProductIdDesc() {
        return productsDao.findAllByOrderByProductIdDesc();
    }

    @Override
    public Product create(Product product) {
        return productsDao.save(product);
    }

    @Override
    public Product update(int productId, Product product) {
        Product existing = productsDao.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        Product productToUpdate = new ProductBuilder(product)
                .withProductId(productId)
                .withCreatedAt(existing.getCreatedAt())
                .build();

        return productsDao.save(productToUpdate);
    }

    @Override
    public void delete(int productId) {
        Product productToDelete = productsDao.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (!orderDetailsDao.findAllByProduct(productToDelete).isEmpty()) {
            throw new ConflictException("This product is connected to an order. It can't be deleted.");
        }

        productsDao.delete(productToDelete);
    }
}
