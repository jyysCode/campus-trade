package com.campus.trade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.campus.trade.pojo.Favorite;

import java.util.List;

public interface FavoriteService {

    void add(Long productId, Long userId);

    void remove(Long productId, Long userId);

    boolean isFavorited(Long productId, Long userId);

    IPage<Favorite> list(Long userId, int page, int size);

    List<Long> getFavoriteProductIds(Long userId);
}
