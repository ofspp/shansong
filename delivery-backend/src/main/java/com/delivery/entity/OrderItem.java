package com.delivery.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("order_item")
@Schema(description = "订单商品明细")
public class OrderItem {

    @TableId(type = IdType.AUTO)
    @Schema(description = "明细ID，主键自增")
    private Long id;

    @Schema(description = "所属订单ID")
    private Long orderId;

    @Schema(description = "菜品ID")
    private Long dishId;

    @Schema(description = "菜品名称（快照，防止后续修改影响历史订单）")
    private String dishName;

    @Schema(description = "菜品图片URL（快照）")
    private String dishImage;

    @Schema(description = "下单时单价（元）")
    private BigDecimal price;

    @Schema(description = "购买数量")
    private Integer quantity;

    @Schema(description = "小计金额（单价×数量）")
    private BigDecimal subtotal;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
