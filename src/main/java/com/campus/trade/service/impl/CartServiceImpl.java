package com.campus.trade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.campus.trade.exception.BusinessException;
import com.campus.trade.mapper.CartMapper;
import com.campus.trade.mapper.ProductMapper;
import com.campus.trade.pojo.Cart;
import com.campus.trade.pojo.Product;
import com.campus.trade.service.CartService;
import com.campus.trade.vo.CartVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CartServiceImpl implements CartService {

    private final CartMapper cartMapper;
    private final ProductMapper productMapper;

    public CartServiceImpl(CartMapper cartMapper, ProductMapper productMapper) {
        this.cartMapper = cartMapper;
        this.productMapper = productMapper;
    }

    @Override
    public void add(Long productId, Long userId) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        if (!product.getStatus().equals(Product.STATUS_APPROVED)) {
            throw new BusinessException("该商品暂不可加入购物车");
        }

        // 检查是否已在购物车
        QueryWrapper<Cart> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).eq("product_id", productId);
        Cart existing = cartMapper.selectOne(wrapper);

        if (existing != null) {
            // 已存在则数量+1
            existing.setQuantity(existing.getQuantity() + 1);
            existing.setUpdateTime(LocalDateTime.now());
            cartMapper.updateById(existing);
        } else {
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setProductId(productId);
            cart.setQuantity(1);
            cart.setCreateTime(LocalDateTime.now());
            cart.setUpdateTime(LocalDateTime.now());
            cartMapper.insert(cart);
        }
        log.info("商品加入购物车, userId={}, productId={}", userId, productId);
    }

    @Override
    public void updateQuantity(Long productId, Long userId, Integer quantity) {
        QueryWrapper<Cart> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).eq("product_id", productId);
        Cart cart = cartMapper.selectOne(wrapper);
        if (cart == null) {
            throw new BusinessException("购物车中无此商品");
        }
        if (quantity <= 0) {
            cartMapper.deleteById(cart.getId());
        } else {
            cart.setQuantity(quantity);
            cart.setUpdateTime(LocalDateTime.now());
            cartMapper.updateById(cart);
        }
    }

    @Override
    public void remove(Long productId, Long userId) {
        QueryWrapper<Cart> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).eq("product_id", productId);
        cartMapper.delete(wrapper);
        log.info("商品移出购物车, userId={}, productId={}", userId, productId);
    }

    @Override
    public void clear(Long userId) {
        QueryWrapper<Cart> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        cartMapper.delete(wrapper);
        log.info("购物车已清空, userId={}", userId);
    }

    @Override
    public List<CartVO> list(Long userId) {
        QueryWrapper<Cart> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.orderByDesc("create_time");
        List<Cart> carts = cartMapper.selectList(wrapper);
        
        // 转换为 CartVO，包含商品信息
        return carts.stream().map(cart -> {
            CartVO vo = new CartVO();
            vo.setId(cart.getId());
            vo.setUserId(cart.getUserId());
            vo.setProductId(cart.getProductId());
            vo.setQuantity(cart.getQuantity());
            vo.setCreateTime(cart.getCreateTime());
            vo.setUpdateTime(cart.getUpdateTime());
            
            // 获取商品信息
            Product product = productMapper.selectById(cart.getProductId());
            if (product != null) {
                vo.setProductName(product.getName());
                vo.setProductDescription(product.getDescription());
                vo.setProductPrice(product.getPrice());
                vo.setProductOriginalPrice(product.getOriginalPrice());
                vo.setProductImage(product.getImage());
                vo.setProductCategory(product.getCategory());
                vo.setSellerName(product.getSellerName());
                vo.setSellerPhone(product.getSellerPhone());
            }
            
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public int count(Long userId) {
        QueryWrapper<Cart> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        return Math.toIntExact(cartMapper.selectCount(wrapper));
    }
}
