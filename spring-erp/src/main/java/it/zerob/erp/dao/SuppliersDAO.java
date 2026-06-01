package it.zerob.erp.dao;

import it.zerob.erp.model.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SuppliersDAO extends CrudRepository<Supplier, Long> {

    List<Supplier> findAllByOrderBySupplierIdDesc();

    Page<Supplier> findAllByOrderBySupplierIdDesc(Pageable pageable);
}
