package com.campus.trade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.trade.exception.BusinessException;
import com.campus.trade.mapper.ReviewMapper;
import com.campus.trade.pojo.Order;
import com.campus.trade.pojo.Review;
import com.campus.trade.pojo.User;
import com.campus.trade.mapper.OrderMapper;
import com.campus.trade.mapper.UserMapper;
import com.campus.trade.service.ReviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewMapper reviewMapper;
    private final OrderMapper orderMapper;
    private final UserMapper userMapper;

    public ReviewServiceImpl(ReviewMapper reviewMapper, OrderMapper orderMapper, UserMapper userMapper) {
        this.reviewMapper = reviewMapper;
        this.orderMapper = orderMapper;
        this.userMapper = userMapper;
    }

    @Override
    public void add(Long orderId, Long productId, Long userId, Integer rating, String content) {
        // 校验订单是否存在且属于当前用户
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("只能评价自己的订单");
        }
        if (!order.getStatus().equals(Order.STATUS_COMPLETED)) {
            throw new BusinessException("只能评价已完成的订单");
        }

        // 检查是否已评价
        QueryWrapper<Review> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id", orderId);
        if (reviewMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("该订单已评价");
        }

        // 校验评分范围
        if (rating == null || rating < 1 || rating > 5) {
            throw new BusinessException("评分必须在1-5之间");
        }

        User user = userMapper.selectById(userId);
        Review review = new Review();
        review.setOrderId(orderId);
        review.setProductId(productId);
        review.setUserId(userId);
        review.setUsername(user != null ? user.getUsername() : "未知用户");
        review.setRating(rating);
        review.setContent(content);
        review.setCreateTime(LocalDateTime.now());
        reviewMapper.insert(review);
        log.info("用户评价订单, orderId={}, userId={}, rating={}", orderId, userId, rating);
    }

    @Override
    public IPage<Review> getByProductId(Long productId, int page, int size) {
        Page<Review> pageParam = new Page<>(page, size);
        QueryWrapper<Review> wrapper = new QueryWrapper<>();
        wrapper.eq("product_id", productId);
        wrapper.orderByDesc("create_time");
        return reviewMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public Review getByOrderId(Long orderId) {
        QueryWrapper<Review> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id", orderId);
        return reviewMapper.selectOne(wrapper);
    }
}
