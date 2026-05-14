package com.delivery.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("dish")
@Schema(description = "菜品信息")
public class Dish {

    @TableId(type = IdType.AUTO)
    @Schema(description = "菜品ID，主键自增")
    private Long id;

    @Schema(description = "所属商家ID")
    private Long merchantId;

    @Schema(description = "所属分类ID")
    private Long categoryId;

    @Schema(description = "菜品名称", example = "宫保鸡丁")
    private String name;

    @Schema(description = "菜品图片URL")
    private String image;

    @Schema(description = "菜品描述/介绍")
    private String description;

    @Schema(description = "单价（元）", example = "28.00")
    private BigDecimal price;

    @Schema(description = "原价（元），用于显示折扣效果")
    private BigDecimal originalPrice;

    @Schema(description = "上架状态：0-下架，1-上架")
    private Integer status;

    @Schema(description = "排序权重，数值越大越靠前")
    private Integer sort;

    @Schema(description = "销量")
    private Integer sales;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @TableLogic
    @Schema(description = "逻辑删除标记")
    private Integer deleted;
}
