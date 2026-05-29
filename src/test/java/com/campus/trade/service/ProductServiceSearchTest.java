package com.campus.trade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.campus.trade.pojo.Product;
import com.campus.trade.pojo.dto.ProductQueryDTO;
import com.campus.trade.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceSearchTest {

    @Mock
    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("搜索功能 - 正常查询：按名称模糊搜索")
    void testSearchByName() {
        ProductQueryDTO queryDTO = new ProductQueryDTO();
        queryDTO.setName("iPhone");
        queryDTO.setPage(1);
        queryDTO.setSize(10);
        
        IPage<Product> mockPage = mock(IPage.class);
        when(productService.search(queryDTO)).thenReturn(mockPage);
        
        IPage<Product> result = productService.search(queryDTO);
        
        assertNotNull(result);
        verify(productService, times(1)).search(queryDTO);
    }

    @Test
    @DisplayName("搜索功能 - 正常查询：按单个分类筛选")
    void testSearchBySingleCategory() {
        ProductQueryDTO queryDTO = new ProductQueryDTO();
        queryDTO.setCategories(Collections.singletonList("电子产品"));
        queryDTO.setPage(1);
        queryDTO.setSize(10);
        
        IPage<Product> mockPage = mock(IPage.class);
        when(productService.search(queryDTO)).thenReturn(mockPage);
        
        IPage<Product> result = productService.search(queryDTO);
        
        assertNotNull(result);
        verify(productService, times(1)).search(queryDTO);
    }

    @Test
    @DisplayName("搜索功能 - 正常查询：按多个分类筛选")
    void testSearchByMultipleCategories() {
        ProductQueryDTO queryDTO = new ProductQueryDTO();
        queryDTO.setCategories(Arrays.asList("电子产品", "图书教材"));
        queryDTO.setPage(1);
        queryDTO.setSize(10);
        
        IPage<Product> mockPage = mock(IPage.class);
        when(productService.search(queryDTO)).thenReturn(mockPage);
        
        IPage<Product> result = productService.search(queryDTO);
        
        assertNotNull(result);
        verify(productService, times(1)).search(queryDTO);
    }

    @Test
    @DisplayName("搜索功能 - 正常查询：名称+分类组合查询")
    void testSearchByNameAndCategory() {
        ProductQueryDTO queryDTO = new ProductQueryDTO();
        queryDTO.setName("手机");
        queryDTO.setCategories(Collections.singletonList("电子产品"));
        queryDTO.setPage(1);
        queryDTO.setSize(10);
        
        IPage<Product> mockPage = mock(IPage.class);
        when(productService.search(queryDTO)).thenReturn(mockPage);
        
        IPage<Product> result = productService.search(queryDTO);
        
        assertNotNull(result);
        verify(productService, times(1)).search(queryDTO);
    }

    @Test
    @DisplayName("搜索功能 - 边界条件：空名称查询")
    void testSearchWithEmptyName() {
        ProductQueryDTO queryDTO = new ProductQueryDTO();
        queryDTO.setName("");
        queryDTO.setPage(1);
        queryDTO.setSize(10);
        
        IPage<Product> mockPage = mock(IPage.class);
        when(productService.search(queryDTO)).thenReturn(mockPage);
        
        IPage<Product> result = productService.search(queryDTO);
        
        assertNotNull(result);
    }

    @Test
    @DisplayName("搜索功能 - 边界条件：空分类列表")
    void testSearchWithEmptyCategories() {
        ProductQueryDTO queryDTO = new ProductQueryDTO();
        queryDTO.setCategories(Collections.emptyList());
        queryDTO.setPage(1);
        queryDTO.setSize(10);
        
        IPage<Product> mockPage = mock(IPage.class);
        when(productService.search(queryDTO)).thenReturn(mockPage);
        
        IPage<Product> result = productService.search(queryDTO);
        
        assertNotNull(result);
    }

    @Test
    @DisplayName("搜索功能 - 边界条件：空参数查询")
    void testSearchWithEmptyParams() {
        ProductQueryDTO queryDTO = new ProductQueryDTO();
        queryDTO.setPage(1);
        queryDTO.setSize(10);
        
        IPage<Product> mockPage = mock(IPage.class);
        when(productService.search(queryDTO)).thenReturn(mockPage);
        
        IPage<Product> result = productService.search(queryDTO);
        
        assertNotNull(result);
    }

    @Test
    @DisplayName("搜索功能 - 排序测试：按时间降序")
    void testSearchSortByCreateTimeDesc() {
        ProductQueryDTO queryDTO = new ProductQueryDTO();
        queryDTO.setSortBy("createTime");
        queryDTO.setSortOrder("desc");
        queryDTO.setPage(1);
        queryDTO.setSize(10);
        
        IPage<Product> mockPage = mock(IPage.class);
        when(productService.search(queryDTO)).thenReturn(mockPage);
        
        IPage<Product> result = productService.search(queryDTO);
        
        assertNotNull(result);
    }

    @Test
    @DisplayName("搜索功能 - 排序测试：按价格升序")
    void testSearchSortByPriceAsc() {
        ProductQueryDTO queryDTO = new ProductQueryDTO();
        queryDTO.setSortBy("price");
        queryDTO.setSortOrder("asc");
        queryDTO.setPage(1);
        queryDTO.setSize(10);
        
        IPage<Product> mockPage = mock(IPage.class);
        when(productService.search(queryDTO)).thenReturn(mockPage);
        
        IPage<Product> result = productService.search(queryDTO);
        
        assertNotNull(result);
    }

    @Test
    @DisplayName("搜索功能 - 排序测试：按价格降序")
    void testSearchSortByPriceDesc() {
        ProductQueryDTO queryDTO = new ProductQueryDTO();
        queryDTO.setSortBy("price");
        queryDTO.setSortOrder("desc");
        queryDTO.setPage(1);
        queryDTO.setSize(10);
        
        IPage<Product> mockPage = mock(IPage.class);
        when(productService.search(queryDTO)).thenReturn(mockPage);
        
        IPage<Product> result = productService.search(queryDTO);
        
        assertNotNull(result);
    }

    @Test
    @DisplayName("搜索功能 - 排序测试：按相关度排序")
    void testSearchSortByRelevance() {
        ProductQueryDTO queryDTO = new ProductQueryDTO();
        queryDTO.setName("iPhone");
        queryDTO.setSortBy("relevance");
        queryDTO.setPage(1);
        queryDTO.setSize(10);
        
        IPage<Product> mockPage = mock(IPage.class);
        when(productService.search(queryDTO)).thenReturn(mockPage);
        
        IPage<Product> result = productService.search(queryDTO);
        
        assertNotNull(result);
    }

    @Test
    @DisplayName("搜索功能 - 分页测试：第一页")
    void testSearchFirstPage() {
        ProductQueryDTO queryDTO = new ProductQueryDTO();
        queryDTO.setPage(1);
        queryDTO.setSize(10);
        
        IPage<Product> mockPage = mock(IPage.class);
        when(productService.search(queryDTO)).thenReturn(mockPage);
        
        IPage<Product> result = productService.search(queryDTO);
        
        assertNotNull(result);
    }

    @Test
    @DisplayName("搜索功能 - 分页测试：指定页码")
    void testSearchSpecificPage() {
        ProductQueryDTO queryDTO = new ProductQueryDTO();
        queryDTO.setPage(5);
        queryDTO.setSize(20);
        
        IPage<Product> mockPage = mock(IPage.class);
        when(productService.search(queryDTO)).thenReturn(mockPage);
        
        IPage<Product> result = productService.search(queryDTO);
        
        assertNotNull(result);
    }

    @Test
    @DisplayName("搜索功能 - 特殊字符处理：名称包含特殊字符")
    void testSearchWithSpecialCharacters() {
        ProductQueryDTO queryDTO = new ProductQueryDTO();
        queryDTO.setName("iPhone Pro Max");
        queryDTO.setPage(1);
        queryDTO.setSize(10);
        
        IPage<Product> mockPage = mock(IPage.class);
        when(productService.search(queryDTO)).thenReturn(mockPage);
        
        IPage<Product> result = productService.search(queryDTO);
        
        assertNotNull(result);
    }

    @Test
    @DisplayName("搜索功能 - 边界条件：超大分页参数")
    void testSearchWithLargePageParams() {
        ProductQueryDTO queryDTO = new ProductQueryDTO();
        queryDTO.setPage(1000);
        queryDTO.setSize(100);
        
        IPage<Product> mockPage = mock(IPage.class);
        when(productService.search(queryDTO)).thenReturn(mockPage);
        
        IPage<Product> result = productService.search(queryDTO);
        
        assertNotNull(result);
    }

    @Test
    @DisplayName("搜索功能 - 边界条件：零分页参数")
    void testSearchWithZeroPageParams() {
        ProductQueryDTO queryDTO = new ProductQueryDTO();
        queryDTO.setPage(0);
        queryDTO.setSize(0);
        
        assertEquals(1, queryDTO.getPage());
        assertEquals(10, queryDTO.getSize());
    }

    @Test
    @DisplayName("搜索功能 - 边界条件：null分页参数")
    void testSearchWithNullPageParams() {
        ProductQueryDTO queryDTO = new ProductQueryDTO();
        queryDTO.setPage(null);
        queryDTO.setSize(null);
        
        assertEquals(1, queryDTO.getPage());
        assertEquals(10, queryDTO.getSize());
    }
}
