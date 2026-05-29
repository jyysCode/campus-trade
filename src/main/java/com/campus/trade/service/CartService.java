package com.campus.trade.service;

import com.campus.trade.vo.CartVO;

import java.util.List;

public interface CartService {

    void add(Long productId, Long userId);

    void updateQuantity(Long productId, Long userId, Integer quantity);

    void remove(Long productId, Long userId);

    void clear(Long userId);

    List<CartVO> list(Long userId);

    int count(Long userId);
}
