package com.campus.trade.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private Long id;

    @NotBlank(message = "商品名称不能为空")
    private String name;

    @NotBlank(message = "商品分类不能为空")
    private String category;

    @NotNull(message = "商品价格不能为空")
    @Positive(message = "价格必须大于0")
    private BigDecimal price;

    private BigDecimal originalPrice;

    @NotNull(message = "商品库存不能为空")
    @Positive(message = "库存必须大于0")
    private Integer stock;

    private String image;

    private String description;

    private String location;

    private String condition;
}
