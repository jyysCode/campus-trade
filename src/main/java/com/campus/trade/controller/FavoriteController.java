package com.campus.trade.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.campus.trade.pojo.Favorite;
import com.campus.trade.pojo.Product;
import com.campus.trade.pojo.Result;
import com.campus.trade.service.FavoriteService;
import com.campus.trade.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/favorite")
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final ProductService productService;

    public FavoriteController(FavoriteService favoriteService, ProductService productService) {
        this.favoriteService = favoriteService;
        this.productService = productService;
    }

    @PostMapping("/add/{productId}")
    public Result<String> add(@PathVariable Long productId, @RequestAttribute("userId") Long userId) {
        favoriteService.add(productId, userId);
        return Result.success("收藏成功");
    }

    @DeleteMapping("/remove/{productId}")
    public Result<String> remove(@PathVariable Long productId, @RequestAttribute("userId") Long userId) {
        favoriteService.remove(productId, userId);
        return Result.success("取消收藏成功");
    }

    @GetMapping("/check/{productId}")
    public Result<Boolean> check(@PathVariable Long productId, @RequestAttribute("userId") Long userId) {
        boolean favorited = favoriteService.isFavorited(productId, userId);
        return Result.success(favorited);
    }

    @GetMapping("/list")
    public Result<IPage<Map<String, Object>>> list(
            @RequestAttribute("userId") Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        IPage<Favorite> favoritePage = favoriteService.list(userId, page, size);

        // 转换为包含商品详情的分页对象
        List<Map<String, Object>> records = favoritePage.getRecords().stream().map(favorite -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", favorite.getId());
            map.put("userId", favorite.getUserId());
            map.put("productId", favorite.getProductId());
            map.put("createTime", favorite.getCreateTime());

            // 查询商品详情
            Product product = productService.getById(favorite.getProductId());
            if (product != null) {
                map.put("name", product.getName());
                map.put("price", product.getPrice());
                map.put("image", product.getImage());
                map.put("sellerName", product.getSellerName());
                map.put("category", product.getCategory());
            }
            return map;
        }).collect(Collectors.toList());

        // 创建新的分页对象
        IPage<Map<String, Object>> resultPage = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>();
        resultPage.setCurrent(favoritePage.getCurrent());
        resultPage.setSize(favoritePage.getSize());
        resultPage.setTotal(favoritePage.getTotal());
        resultPage.setPages(favoritePage.getPages());
        resultPage.setRecords(records);

        return Result.success(resultPage);
    }

    @GetMapping("/product-ids")
    public Result<List<Long>> productIds(@RequestAttribute("userId") Long userId) {
        List<Long> ids = favoriteService.getFavoriteProductIds(userId);
        return Result.success(ids);
    }
}
