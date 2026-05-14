package com.delivery.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("customer")
@Schema(description = "顾客/用户信息")
public class Customer {

    @TableId(type = IdType.AUTO)
    @Schema(description = "顾客ID，主键自增")
    private Long id;

    @Schema(description = "登录用户名", example = "customer1")
    private String username;

    @Schema(description = "登录密码（BCrypt加密存储）")
    private String password;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "手机号码")
    private String phone;

    @Schema(description = "头像URL")
    private String avatar;

    @Schema(description = "账号状态：0-禁用，1-正常")
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @TableLogic
    @Schema(description = "逻辑删除：0-未删除，1-已删除")
    private Integer deleted;
}
