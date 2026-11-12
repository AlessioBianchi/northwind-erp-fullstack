package com.github.alessiobianchi.erp.service;

import com.github.alessiobianchi.erp.model.Supplier;
import org.springframework.data.domain.Page;

import java.util.List;

public interface SuppliersService {

    List<Supplier> findAllByOrderBySupplierIdDesc();

    Page<Supplier> findAllByOrderBySupplierIdDesc(int pageNumber, int pageSize);

    Supplier create(Supplier supplier);

    Supplier update(int supplierId, Supplier supplier);

    void delete(int supplierId);
}
