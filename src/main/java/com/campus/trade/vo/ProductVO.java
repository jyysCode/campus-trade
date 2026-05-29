package com.campus.trade.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品视图对象，包含卖家昵称等扩展信息
 */
@Data
public class ProductVO {
    private Long id;
    private String name;
    private String category;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer stock;
    private Integer sales;
    private Integer views;
    private String image;
    private String description;
    private String location;
    private String condition;
    private Long sellerId;
    private String sellerName;
    private String sellerNickname;
    private String sellerPhone;
    private String sellerAvatar;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
