package com.github.alessiobianchi.erp.dao;

import com.github.alessiobianchi.erp.model.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SuppliersDAO extends CrudRepository<Supplier, Integer> {

    List<Supplier> findAllByOrderBySupplierIdDesc();

    Page<Supplier> findAllByOrderBySupplierIdDesc(Pageable pageable);

    @Query("""
            select s from Supplier s
            where lower(s.companyName) like lower(concat('%', :search, '%'))
               or lower(s.contactName) like lower(concat('%', :search, '%'))
            order by s.supplierId desc""")
    Page<Supplier> search(@Param("search") String search, Pageable pageable);
}
