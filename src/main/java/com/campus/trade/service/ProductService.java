package com.campus.trade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.campus.trade.pojo.Product;
import com.campus.trade.pojo.dto.ProductDTO;
import com.campus.trade.pojo.dto.ProductQueryDTO;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {

    void add(ProductDTO productDTO, Long userId);

    void update(ProductDTO productDTO, Long userId);

    void delete(Long id, Long userId);

    Product getById(Long id);

    IPage<Product> list(int page, int size, String name, String category, BigDecimal minPrice, BigDecimal maxPrice);

    List<Product> getHotProducts();

    IPage<Product> getByUserId(Long userId, int page, int size);

    void approve(Long id);

    void reject(Long id);

    IPage<Product> listPending(int page, int size);

    void initTestData();

    void adminAdd(ProductDTO productDTO);

    void adminUpdate(ProductDTO productDTO);

    void adminDelete(Long id);

    List<String> getAllCategories();

    IPage<Product> search(ProductQueryDTO queryDTO);
}
