package com.github.alessiobianchi.erp.service;

import com.github.alessiobianchi.erp.dao.ProductsDAO;
import com.github.alessiobianchi.erp.dao.SuppliersDAO;
import com.github.alessiobianchi.erp.model.Supplier;
import com.github.alessiobianchi.erp.model.SupplierBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SuppliersService {

    private final SuppliersDAO suppliersDao;
    private final ProductsDAO productsDao;

    public SuppliersService(SuppliersDAO suppliersDao, ProductsDAO productsDao) {
        this.suppliersDao = suppliersDao;
        this.productsDao = productsDao;
    }

    public List<Supplier> findAllByOrderBySupplierIdDesc() {
        return suppliersDao.findAllByOrderBySupplierIdDesc();
    }

    public Page<Supplier> findAllByOrderBySupplierIdDesc(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return suppliersDao.findAllByOrderBySupplierIdDesc(pageable);
    }

    public Supplier create(Supplier supplier) {
        return suppliersDao.save(supplier);
    }

    public Supplier update(int supplierId, Supplier supplier) {
        Supplier supplierToUpdate = new SupplierBuilder(supplier)
                .withSupplierId(supplierId)
                .build();

        return suppliersDao.save(supplierToUpdate);
    }

    public boolean delete(int supplierId) {
        Supplier supplierToDelete = new SupplierBuilder()
                .withSupplierId(supplierId)
                .build();

        if (!productsDao.findAllBySupplier(supplierToDelete).isEmpty()) {
            return false;
        }

        suppliersDao.delete(supplierToDelete);
        return true;
    }
}
