package com.delivery.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "登录请求参数")
public class LoginDto {

    @Schema(description = "用户名", example = "customer1")
    private String username;

    @Schema(description = "密码", example = "123456")
    private String password;
}
