package com.campus.trade.controller;

import com.campus.trade.pojo.Result;
import com.campus.trade.service.CartService;
import com.campus.trade.vo.CartVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/add/{productId}")
    public Result<String> add(@PathVariable Long productId, @RequestAttribute("userId") Long userId) {
        cartService.add(productId, userId);
        return Result.success("已加入购物车");
    }

    @PutMapping("/update/{productId}")
    public Result<String> updateQuantity(@PathVariable Long productId,
                                         @RequestParam Integer quantity,
                                         @RequestAttribute("userId") Long userId) {
        cartService.updateQuantity(productId, userId, quantity);
        return Result.success("更新成功");
    }

    @DeleteMapping("/remove/{productId}")
    public Result<String> remove(@PathVariable Long productId, @RequestAttribute("userId") Long userId) {
        cartService.remove(productId, userId);
        return Result.success("已移出购物车");
    }

    @DeleteMapping("/clear")
    public Result<String> clear(@RequestAttribute("userId") Long userId) {
        cartService.clear(userId);
        return Result.success("购物车已清空");
    }

    @GetMapping("/list")
    public Result<List<CartVO>> list(@RequestAttribute("userId") Long userId) {
        List<CartVO> carts = cartService.list(userId);
        return Result.success(carts);
    }

    @GetMapping("/count")
    public Result<Integer> count(@RequestAttribute("userId") Long userId) {
        int count = cartService.count(userId);
        return Result.success(count);
    }
}
