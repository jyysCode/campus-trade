package com.campus.trade.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 购物车视图对象，包含商品信息
 */
@Data
public class CartVO {
    private Long id;
    private Long userId;
    private Long productId;
    private Integer quantity;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    // 商品信息
    private String productName;
    private String productDescription;
    private BigDecimal productPrice;
    private BigDecimal productOriginalPrice;
    private String productImage;
    private String productCategory;
    private String sellerName;
    private String sellerPhone;
}
