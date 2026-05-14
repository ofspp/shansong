package com.delivery.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("evaluation")
@Schema(description = "订单评价信息")
public class Evaluation {

    @TableId(type = IdType.AUTO)
    @Schema(description = "评价ID，主键自增")
    private Long id;

    @Schema(description = "关联的订单ID")
    private Long orderId;

    @Schema(description = "评价顾客ID")
    private Long customerId;

    @Schema(description = "被评价商家ID")
    private Long merchantId;

    @Schema(description="评分：1-5分", example = "5")
    private Integer rating;

    @Schema(description = "评价内容")
    private String content;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @TableLogic
    @Schema(description = "逻辑删除标记")
    private Integer deleted;
}
