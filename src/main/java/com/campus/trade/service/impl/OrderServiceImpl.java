package com.campus.trade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.trade.exception.BusinessException;
import com.campus.trade.mapper.OrderMapper;
import com.campus.trade.mapper.ProductMapper;
import com.campus.trade.pojo.Order;
import com.campus.trade.pojo.Product;
import com.campus.trade.pojo.dto.OrderDTO;
import com.campus.trade.service.OrderService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final ProductMapper productMapper;

    public OrderServiceImpl(OrderMapper orderMapper, ProductMapper productMapper) {
        this.orderMapper = orderMapper;
        this.productMapper = productMapper;
    }

    @Override
    public void create(OrderDTO orderDTO, Long userId) {
        Product product = productMapper.selectById(orderDTO.getProductId());
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        if (product.getStock() < orderDTO.getQuantity()) {
            throw new BusinessException("库存不足");
        }
        Order order = new Order();
        order.setProductId(product.getId());
        order.setProductName(product.getName());
        order.setProductImage(product.getImage());
        order.setUserId(userId);
        order.setSellerId(product.getSellerId());
        order.setQuantity(orderDTO.getQuantity());
        order.setPrice(product.getPrice());
        order.setTotalAmount(product.getPrice().multiply(BigDecimal.valueOf(orderDTO.getQuantity())));
        order.setStatus(0);
        order.setAddress(orderDTO.getAddress());
        order.setRemark(orderDTO.getRemark());
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.insert(order);
        product.setStock(product.getStock() - orderDTO.getQuantity());
        productMapper.updateById(product);
    }

    @Override
    public void updateStatus(Long id, Integer status, Long userId) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getUserId().equals(userId) && !order.getSellerId().equals(userId)) {
            throw new BusinessException("无权修改该订单");
        }
        order.setStatus(status);
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    @Override
    public Order getById(Long id) {
        return orderMapper.selectById(id);
    }

    @Override
    public IPage<Order> getBuyerOrders(Long userId, int page, int size) {
        Page<Order> pageParam = new Page<>(page, size);
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.orderByDesc("create_time");
        return orderMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public IPage<Order> getSellerOrders(Long userId, int page, int size) {
        Page<Order> pageParam = new Page<>(page, size);
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq("seller_id", userId);
        wrapper.orderByDesc("create_time");
        return orderMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public List<Order> getAllOrders() {
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("create_time");
        return orderMapper.selectList(wrapper);
    }
}