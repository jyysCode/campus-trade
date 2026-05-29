
package com.campus.trade.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.campus.trade.pojo.Order;
import com.campus.trade.pojo.Result;
import com.campus.trade.pojo.dto.OrderDTO;
import com.campus.trade.service.OrderService;

import java.util.List;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
public class OrderController {
    
    private final OrderService orderService;
    
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    
    @PostMapping("/create")
    public Result<String> create(@Valid @RequestBody OrderDTO orderDTO, @RequestAttribute("userId") Long userId) {
        orderService.create(orderDTO, userId);
        return Result.success("下单成功");
    }
    
    @PutMapping("/status/{id}")
    public Result<String> updateStatus(@PathVariable Long id, @RequestParam Integer status, @RequestAttribute("userId") Long userId) {
        orderService.updateStatus(id, status, userId);
        return Result.success("更新成功");
    }
    
    @GetMapping("/detail/{id}")
    public Result<Order> detail(@PathVariable Long id) {
        Order order = orderService.getById(id);
        return Result.success(order);
    }
    
    @GetMapping("/buyer")
    public Result<IPage<Order>> buyerOrders(
            @RequestAttribute("userId") Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        IPage<Order> result = orderService.getBuyerOrders(userId, page, size);
        return Result.success(result);
    }
    
    @GetMapping("/seller")
    public Result<IPage<Order>> sellerOrders(
            @RequestAttribute("userId") Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        IPage<Order> result = orderService.getSellerOrders(userId, page, size);
        return Result.success(result);
    }
    
    @GetMapping("/all")
    public Result<List<Order>> allOrders() {
        List<Order> orders = orderService.getAllOrders();
        return Result.success(orders);
    }
}
