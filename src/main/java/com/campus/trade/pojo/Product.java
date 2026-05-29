package com.campus.trade.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("product")
public class Product {

    public static final int STATUS_PENDING = 0;
    public static final int STATUS_APPROVED = 1;
    public static final int STATUS_REJECTED = 2;

    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String category;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer stock;
    private String image;
    private String description;
    private String location;
    @TableField("`condition`")
    private String condition;
    private Long sellerId;
    private String sellerName;
    private String sellerPhone;
    private Integer status;
    private Integer deleted;
    private Integer views;
    private Integer sales;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}