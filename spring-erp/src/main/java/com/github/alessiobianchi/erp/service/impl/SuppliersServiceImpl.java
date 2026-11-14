package com.github.alessiobianchi.erp.service.impl;

import com.github.alessiobianchi.erp.dao.ProductsDAO;
import com.github.alessiobianchi.erp.dao.SuppliersDAO;
import com.github.alessiobianchi.erp.exception.ConflictException;
import com.github.alessiobianchi.erp.exception.ResourceNotFoundException;
import com.github.alessiobianchi.erp.model.Supplier;
import com.github.alessiobianchi.erp.model.SupplierBuilder;
import com.github.alessiobianchi.erp.service.SuppliersService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class SuppliersServiceImpl implements SuppliersService {

    private final SuppliersDAO suppliersDao;
    private final ProductsDAO productsDao;

    public SuppliersServiceImpl(SuppliersDAO suppliersDao, ProductsDAO productsDao) {
        this.suppliersDao = suppliersDao;
        this.productsDao = productsDao;
    }

    @Override
    public List<Supplier> findAllByOrderBySupplierIdDesc() {
        return suppliersDao.findAllByOrderBySupplierIdDesc();
    }

    @Override
    public Page<Supplier> findAllByOrderBySupplierIdDesc(int pageNumber, int pageSize, String search) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return StringUtils.hasText(search)
                ? suppliersDao.search(search.trim(), pageable)
                : suppliersDao.findAllByOrderBySupplierIdDesc(pageable);
    }

    @Override
    public Supplier create(Supplier supplier) {
        return suppliersDao.save(supplier);
    }

    @Override
    public Supplier update(int supplierId, Supplier supplier) {
        Supplier existing = suppliersDao.findById(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found"));

        Supplier supplierToUpdate = new SupplierBuilder(supplier)
                .withSupplierId(supplierId)
                .withCreatedAt(existing.getCreatedAt())
                .build();

        return suppliersDao.save(supplierToUpdate);
    }

    @Override
    public void delete(int supplierId) {
        Supplier supplierToDelete = suppliersDao.findById(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found"));

        if (!productsDao.findAllBySupplier(supplierToDelete).isEmpty()) {
            throw new ConflictException("There are products connected to this supplier. It can't be deleted.");
        }

        suppliersDao.delete(supplierToDelete);
    }
}
