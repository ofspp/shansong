package com.delivery.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("merchant")
@Schema(description = "商家/店铺信息")
public class Merchant {

    @TableId(type = IdType.AUTO)
    @Schema(description = "商家ID，主键自增")
    private Long id;

    @Schema(description = "登录用户名")
    private String username;

    @Schema(description = "登录密码（BCrypt加密存储）")
    private String password;

    @Schema(description = "店铺名称", example = "美味餐厅")
    private String shopName;

    @Schema(description = "店铺Logo图片URL")
    private String shopLogo;

    @Schema(description = "联系电话")
    private String phone;

    @Schema(description = "所在省份")
    private String province;

    @Schema(description = "所在城市")
    private String city;

    @Schema(description = "所在区县")
    private String district;

    @Schema(description = "详细地址")
    private String address;

    @Schema(description = "营业时间", example = "08:00-22:00")
    private String businessHours;

    @Schema(description = "配送费（元）", example = "5.00")
    private BigDecimal deliveryFee;

    @Schema(description = "起送金额（元）", example = "20.00")
    private BigDecimal minOrderAmount;

    @Schema(description = "点赞数")
    private Integer likesCount;

    @Schema(description = "审核状态：0-待审核，1-营业中，2-已禁用")
    private Integer status;

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
