package com.github.alessiobianchi.erp.service.impl;

import com.github.alessiobianchi.erp.converter.SupplierBeanToDtoConverter;
import com.github.alessiobianchi.erp.converter.SupplierDtoToBeanConverter;
import com.github.alessiobianchi.erp.dao.ProductsDAO;
import com.github.alessiobianchi.erp.dao.SuppliersDAO;
import com.github.alessiobianchi.erp.dto.SupplierDTO;
import com.github.alessiobianchi.erp.exception.ConflictException;
import com.github.alessiobianchi.erp.exception.ResourceNotFoundException;
import com.github.alessiobianchi.erp.model.Supplier;
import com.github.alessiobianchi.erp.model.SupplierBuilder;
import com.github.alessiobianchi.erp.service.SuppliersService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class SuppliersServiceImpl implements SuppliersService {

    private final SuppliersDAO suppliersDao;
    private final ProductsDAO productsDao;

    public SuppliersServiceImpl(SuppliersDAO suppliersDao, ProductsDAO productsDao) {
        this.suppliersDao = suppliersDao;
        this.productsDao = productsDao;
    }

    @Override
    public List<SupplierDTO> findAllByOrderBySupplierIdDesc() {
        return suppliersDao.findAllByOrderBySupplierIdDesc().stream()
                .map(SupplierBeanToDtoConverter::convert)
                .toList();
    }

    @Override
    public Page<SupplierDTO> findAllByOrderBySupplierIdDesc(int pageNumber, int pageSize, String search) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Supplier> page = StringUtils.hasText(search)
                ? suppliersDao.search(search.trim(), pageable)
                : suppliersDao.findAllByOrderBySupplierIdDesc(pageable);
        return page.map(SupplierBeanToDtoConverter::convert);
    }

    @Override
    @Transactional
    public SupplierDTO create(SupplierDTO supplierDto) {
        return SupplierBeanToDtoConverter.convert(suppliersDao.save(SupplierDtoToBeanConverter.convert(supplierDto)));
    }

    @Override
    @Transactional
    public SupplierDTO update(int supplierId, SupplierDTO supplierDto) {
        Supplier existing = suppliersDao.findById(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found"));

        Supplier supplierToUpdate = new SupplierBuilder(SupplierDtoToBeanConverter.convert(supplierDto))
                .withSupplierId(supplierId)
                .withCreatedAt(existing.getCreatedAt())
                .build();

        return SupplierBeanToDtoConverter.convert(suppliersDao.save(supplierToUpdate));
    }

    @Override
    @Transactional
    public void delete(int supplierId) {
        Supplier supplierToDelete = suppliersDao.findById(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found"));

        if (!productsDao.findAllBySupplier(supplierToDelete).isEmpty()) {
            throw new ConflictException("There are products connected to this supplier. It can't be deleted.");
        }

        suppliersDao.delete(supplierToDelete);
    }
}
