package com.campus.trade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.campus.trade.pojo.Order;
import com.campus.trade.pojo.dto.OrderDTO;

import java.util.List;

public interface OrderService {

    void create(OrderDTO orderDTO, Long userId);

    void updateStatus(Long id, Integer status, Long userId);

    Order getById(Long id);

    IPage<Order> getBuyerOrders(Long userId, int page, int size);

    IPage<Order> getSellerOrders(Long userId, int page, int size);
    
    List<Order> getAllOrders();
}