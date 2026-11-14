package com.github.alessiobianchi.erp.service.impl;

import com.github.alessiobianchi.erp.converter.ProductBeanToDtoConverter;
import com.github.alessiobianchi.erp.converter.ProductDtoToBeanConverter;
import com.github.alessiobianchi.erp.dao.OrderDetailsDAO;
import com.github.alessiobianchi.erp.dao.ProductsDAO;
import com.github.alessiobianchi.erp.dto.ProductDTO;
import com.github.alessiobianchi.erp.exception.ConflictException;
import com.github.alessiobianchi.erp.exception.ResourceNotFoundException;
import com.github.alessiobianchi.erp.model.Product;
import com.github.alessiobianchi.erp.model.ProductBuilder;
import com.github.alessiobianchi.erp.service.ProductsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProductsServiceImpl implements ProductsService {

    private final ProductsDAO productsDao;
    private final OrderDetailsDAO orderDetailsDao;

    public ProductsServiceImpl(ProductsDAO productsDao, OrderDetailsDAO orderDetailsDao) {
        this.productsDao = productsDao;
        this.orderDetailsDao = orderDetailsDao;
    }

    @Override
    public Page<ProductDTO> findAllByOrderByProductIdDesc(int pageNumber, int pageSize, String search) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Product> page = StringUtils.hasText(search)
                ? productsDao.search(search.trim(), pageable)
                : productsDao.findAllByOrderByProductIdDesc(pageable);
        return page.map(ProductBeanToDtoConverter::convert);
    }

    @Override
    public List<ProductDTO> findAllByOrderByProductIdDesc() {
        return productsDao.findAllByOrderByProductIdDesc().stream()
                .map(ProductBeanToDtoConverter::convert)
                .toList();
    }

    @Override
    @Transactional
    public ProductDTO create(ProductDTO productDto) {
        return ProductBeanToDtoConverter.convert(productsDao.save(ProductDtoToBeanConverter.convert(productDto)));
    }

    @Override
    @Transactional
    public ProductDTO update(int productId, ProductDTO productDto) {
        Product existing = productsDao.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        Product productToUpdate = new ProductBuilder(ProductDtoToBeanConverter.convert(productDto))
                .withProductId(productId)
                .withCreatedAt(existing.getCreatedAt())
                .build();

        return ProductBeanToDtoConverter.convert(productsDao.save(productToUpdate));
    }

    @Override
    @Transactional
    public void delete(int productId) {
        Product productToDelete = productsDao.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (!orderDetailsDao.findAllByProduct(productToDelete).isEmpty()) {
            throw new ConflictException("This product is connected to an order. It can't be deleted.");
        }

        productsDao.delete(productToDelete);
    }
}
