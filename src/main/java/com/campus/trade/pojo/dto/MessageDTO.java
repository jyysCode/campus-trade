package com.campus.trade.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {

    @NotNull(message = "接收用户ID不能为空")
    private Long toUserId;

    @NotBlank(message = "消息内容不能为空")
    private String content;

    private Long productId;
}