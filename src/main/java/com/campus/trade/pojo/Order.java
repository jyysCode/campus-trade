package com.campus.trade.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
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
@TableName("`order`")
public class Order {

    public static final int STATUS_PENDING_PAYMENT = 0;
    public static final int STATUS_PAID = 1;
    public static final int STATUS_SHIPPED = 2;
    public static final int STATUS_COMPLETED = 3;
    public static final int STATUS_CANCELLED = 4;

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long productId;
    private String productName;
    private String productImage;
    private Long userId;
    private Long sellerId;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal totalAmount;
    private Integer status;
    private String address;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}