package com.github.alessiobianchi.erp.service;

import com.github.alessiobianchi.erp.dto.ProductDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductsService {

    Page<ProductDTO> findAllByOrderByProductIdDesc(int pageNumber, int pageSize, String search);

    List<ProductDTO> findAllByOrderByProductIdDesc();

    ProductDTO create(ProductDTO productDto);

    ProductDTO update(int productId, ProductDTO productDto);

    void delete(int productId);
}
