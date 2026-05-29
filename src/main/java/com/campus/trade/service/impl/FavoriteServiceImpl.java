package com.campus.trade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.trade.exception.BusinessException;
import com.campus.trade.mapper.FavoriteMapper;
import com.campus.trade.mapper.ProductMapper;
import com.campus.trade.pojo.Favorite;
import com.campus.trade.pojo.Product;
import com.campus.trade.service.FavoriteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteMapper favoriteMapper;
    private final ProductMapper productMapper;

    public FavoriteServiceImpl(FavoriteMapper favoriteMapper, ProductMapper productMapper) {
        this.favoriteMapper = favoriteMapper;
        this.productMapper = productMapper;
    }

    @Override
    public void add(Long productId, Long userId) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        // 检查是否已收藏
        QueryWrapper<Favorite> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).eq("product_id", productId);
        if (favoriteMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("已收藏该商品");
        }

        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setProductId(productId);
        favorite.setCreateTime(LocalDateTime.now());
        favoriteMapper.insert(favorite);
        log.info("用户收藏商品, userId={}, productId={}", userId, productId);
    }

    @Override
    public void remove(Long productId, Long userId) {
        QueryWrapper<Favorite> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).eq("product_id", productId);
        favoriteMapper.delete(wrapper);
        log.info("用户取消收藏, userId={}, productId={}", userId, productId);
    }

    @Override
    public boolean isFavorited(Long productId, Long userId) {
        QueryWrapper<Favorite> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).eq("product_id", productId);
        return favoriteMapper.selectCount(wrapper) > 0;
    }

    @Override
    public IPage<Favorite> list(Long userId, int page, int size) {
        Page<Favorite> pageParam = new Page<>(page, size);
        QueryWrapper<Favorite> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.orderByDesc("create_time");
        return favoriteMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public List<Long> getFavoriteProductIds(Long userId) {
        QueryWrapper<Favorite> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.select("product_id");
        List<Favorite> favorites = favoriteMapper.selectList(wrapper);
        return favorites.stream().map(Favorite::getProductId).collect(Collectors.toList());
    }
}
