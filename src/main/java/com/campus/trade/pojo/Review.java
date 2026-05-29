package com.campus.trade.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 评价实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("review")
public class Review {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orderId;
    private Long productId;
    private Long userId;
    private String username;
    private Integer rating;
    private String content;
    private LocalDateTime createTime;
}
