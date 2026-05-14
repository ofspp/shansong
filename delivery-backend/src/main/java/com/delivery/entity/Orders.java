package com.delivery.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("orders")
@Schema(description = "订单信息")
public class Orders {

    @TableId(type = IdType.AUTO)
    @Schema(description = "订单ID，主键自增")
    private Long id;

    @Schema(description = "订单编号（唯一业务标识）", example = "DD20260406001")
    private String orderNo;

    @Schema(description = "下单顾客ID")
    private Long customerId;

    @Schema(description = "商家ID")
    private Long merchantId;

    @Schema(description = "配送骑手ID（接单后赋值）")
    private Long riderId;

    @Schema(description = "收货地址ID")
    private Long addressId;

    @Schema(description = "收货人姓名")
    private String receiverName;

    @Schema(description = "收货人电话")
    private String receiverPhone;

    @Schema(description = "省")
    private String province;

    @Schema(description = "市")
    private String city;

    @Schema(description = "区")
    private String district;

    @Schema(description = "详细地址")
    private String detailAddress;

    @Schema(description = "订单总金额（元）")
    private BigDecimal totalAmount;

    @Schema(description = "配送费（元）")
    private BigDecimal deliveryFee;

    @Schema(description = "实付金额（元），包含配送费")
    private BigDecimal payAmount;

    @Schema(description = "实际支付金额")
    private BigDecimal actualAmount;

    @Schema(description = "订单状态", example = "PENDING_PAYMENT")
    private String status;

    @Schema(description = "订单备注信息")
    private String remark;

    @Schema(description = "取消原因")
    private String cancelReason;

    @Schema(description = "支付方式：ALIPAY/WX_PAY/OFFLINE")
    private String payMethod;

    @Schema(description = "支付时间")
    private LocalDateTime payTime;

    @Schema(description = "商家接单时间")
    private LocalDateTime acceptTime;

    @Schema(description = "骑手接单时间")
    private LocalDateTime grabTime;

    @Schema(description = "完成时间")
    private LocalDateTime completeTime;

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
