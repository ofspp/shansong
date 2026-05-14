package com.delivery.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("merchant_like")
public class MerchantLike {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long customerId;

    private Long merchantId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
