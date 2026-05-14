package com.delivery.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("cart")
@Schema(description = "购物车商品项")
public class Cart {

    @TableId(type = IdType.AUTO)
    @Schema(description = "购物车记录ID，主键自增")
    private Long id;

    @Schema(description = "所属顾客ID")
    private Long customerId;

    @Schema(description = "商家ID")
    private Long merchantId;

    @Schema(description = "菜品ID")
    private Long dishId;

    @Schema(description = "购买数量")
    private Integer quantity;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "添加时间")
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @TableLogic
    @Schema(description = "逻辑删除标记")
    private Integer deleted;
}
