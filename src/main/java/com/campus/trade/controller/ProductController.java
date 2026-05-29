package com.campus.trade.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.campus.trade.pojo.Product;
import com.campus.trade.pojo.Result;
import com.campus.trade.pojo.dto.ProductDTO;
import com.campus.trade.pojo.dto.ProductQueryDTO;
import com.campus.trade.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/add")
    public Result<String> add(@Valid @RequestBody ProductDTO productDTO, @RequestAttribute("userId") Long userId) {
        productService.add(productDTO, userId);
        return Result.success("发布成功，等待审核");
    }

    @PutMapping("/update")
    public Result<String> update(@Valid @RequestBody ProductDTO productDTO, @RequestAttribute("userId") Long userId) {
        productService.update(productDTO, userId);
        return Result.success("更新成功，等待重新审核");
    }

    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Long id, @RequestAttribute("userId") Long userId) {
        productService.delete(id, userId);
        return Result.success("删除成功");
    }

    @GetMapping("/detail/{id}")
    public Result<Product> detail(@PathVariable Long id) {
        Product product = productService.getById(id);
        return Result.success(product);
    }

    @GetMapping("/list")
    public Result<IPage<Product>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {
        IPage<Product> result = productService.list(page, size, name, category,
                minPrice != null ? java.math.BigDecimal.valueOf(minPrice) : null,
                maxPrice != null ? java.math.BigDecimal.valueOf(maxPrice) : null);
        return Result.success(result);
    }

    @GetMapping("/hot")
    public Result<List<Product>> hot() {
        List<Product> products = productService.getHotProducts();
        return Result.success(products);
    }

    @GetMapping("/my")
    public Result<IPage<Product>> my(
            @RequestAttribute("userId") Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        IPage<Product> result = productService.getByUserId(userId, page, size);
        return Result.success(result);
    }

    @PostMapping("/admin/add")
    public Result<String> adminAdd(@Valid @RequestBody ProductDTO productDTO) {
        productService.adminAdd(productDTO);
        return Result.success("商品添加成功");
    }

    @PutMapping("/admin/update")
    public Result<String> adminUpdate(@Valid @RequestBody ProductDTO productDTO) {
        productService.adminUpdate(productDTO);
        return Result.success("商品更新成功");
    }

    @DeleteMapping("/admin/delete/{id}")
    public Result<String> adminDelete(@PathVariable Long id) {
        productService.adminDelete(id);
        return Result.success("删除成功");
    }

    @PostMapping("/approve/{id}")
    public Result<String> approve(@PathVariable Long id) {
        productService.approve(id);
        return Result.success("审核通过");
    }

    @PostMapping("/reject/{id}")
    public Result<String> reject(@PathVariable Long id) {
        productService.reject(id);
        return Result.success("审核拒绝");
    }

    @GetMapping("/pending")
    public Result<IPage<Product>> listPending(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        IPage<Product> result = productService.listPending(page, size);
        return Result.success(result);
    }

    @GetMapping("/search")
    public Result<IPage<Product>> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) List<String> categories,
            @RequestParam(defaultValue = "createTime") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) java.math.BigDecimal minPrice,
            @RequestParam(required = false) java.math.BigDecimal maxPrice) {

        ProductQueryDTO queryDTO = new ProductQueryDTO();
        queryDTO.setName(name);
        queryDTO.setCategories(categories);
        queryDTO.setSortBy(sortBy);
        queryDTO.setSortOrder(sortOrder);
        queryDTO.setPage(page);
        queryDTO.setSize(size);
        queryDTO.setMinPrice(minPrice);
        queryDTO.setMaxPrice(maxPrice);

        IPage<Product> result = productService.search(queryDTO);
        return Result.success(result);
    }
}
