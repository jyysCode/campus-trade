package com.campus.trade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.campus.trade.pojo.Review;

public interface ReviewService {

    void add(Long orderId, Long productId, Long userId, Integer rating, String content);

    IPage<Review> getByProductId(Long productId, int page, int size);

    Review getByOrderId(Long orderId);
}
