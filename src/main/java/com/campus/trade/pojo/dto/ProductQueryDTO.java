package com.campus.trade.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductQueryDTO {
    
    private String name;
    
    private List<String> categories;
    
    private String sortBy;
    
    private String sortOrder;
    
    private Integer page;
    
    private Integer size;
    
    private BigDecimal minPrice;
    
    private BigDecimal maxPrice;
    
    public Integer getPage() {
        return page == null ? 1 : page;
    }
    
    public Integer getSize() {
        return size == null ? 10 : size;
    }
    
    public String getSortBy() {
        return sortBy == null || sortBy.isEmpty() ? "createTime" : sortBy;
    }
    
    public String getSortOrder() {
        return sortOrder == null || sortOrder.isEmpty() ? "desc" : sortOrder;
    }
}