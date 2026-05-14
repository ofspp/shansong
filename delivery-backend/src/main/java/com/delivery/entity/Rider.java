package com.delivery.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("rider")
@Schema(description = "配送骑手信息")
public class Rider {

    @TableId(type = IdType.AUTO)
    @Schema(description = "骑手ID，主键自增")
    private Long id;

    @Schema(description = "登录用户名")
    private String username;

    @Schema(description = "登录密码（BCrypt加密存储）")
    private String password;

    @Schema(description = "真实姓名")
    private String realName;

    @Schema(description = "手机号码")
    private String phone;

    @Schema(description = "身份证号")
    private String idCard;

    @Schema(description = "车辆类型：电动车/摩托车/汽车")
    private String vehicleType;

    @Schema(description = "车牌号")
    private String vehicleNo;

    @Schema(description = "头像URL")
    private String avatar;

    @Schema(description = "工作状态：0-休息中，1-接单中")
    private Integer workingStatus;

    @Schema(description = "审核状态：0-待审核，1-已通过，2-已禁用")
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
