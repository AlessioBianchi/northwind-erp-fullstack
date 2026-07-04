package com.github.alessiobianchi.erp.dao;

import com.github.alessiobianchi.erp.model.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SuppliersDAO extends CrudRepository<Supplier, Integer> {

    List<Supplier> findAllByOrderBySupplierIdDesc();

    Page<Supplier> findAllByOrderBySupplierIdDesc(Pageable pageable);
}
