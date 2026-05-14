package com.delivery.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("category")
@Schema(description = "菜品分类")
public class Category {

    @TableId(type = IdType.AUTO)
    @Schema(description = "分类ID，主键自增")
    private Long id;

    @Schema(description = "所属商家ID")
    private Long merchantId;

    @Schema(description = "分类名称", example = "热销推荐")
    private String name;

    @Schema(description = "排序号，数值越小越靠前")
    private Integer sort;

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
