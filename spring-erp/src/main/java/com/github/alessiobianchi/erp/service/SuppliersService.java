package com.github.alessiobianchi.erp.service;

import com.github.alessiobianchi.erp.dto.SupplierDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface SuppliersService {

    List<SupplierDTO> findAllByOrderBySupplierIdDesc();

    Page<SupplierDTO> findAllByOrderBySupplierIdDesc(int pageNumber, int pageSize, String search);

    SupplierDTO create(SupplierDTO supplierDto);

    SupplierDTO update(int supplierId, SupplierDTO supplierDto);

    void delete(int supplierId);
}
