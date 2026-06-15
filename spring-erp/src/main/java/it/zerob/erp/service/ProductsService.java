package it.zerob.erp.service;

import it.zerob.erp.dao.OrderDetailsDAO;
import it.zerob.erp.dao.ProductsDAO;
import it.zerob.erp.model.Product;
import it.zerob.erp.model.ProductBuilder;
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

    public Product save(Product product) {
        return productsDao.save(product);
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
