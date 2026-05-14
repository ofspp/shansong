package com.delivery.controller;

import com.delivery.common.Result;
import com.delivery.dto.LoginDto;
import com.delivery.dto.RegisterDto;
import com.delivery.service.AuthService;
import com.delivery.vo.LoginVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "用户登录、注册相关接口")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "管理员登录", description = "系统管理员使用用户名和密码进行登录，返回JWT令牌")
    @PostMapping("/admin/login")
    public Result<LoginVo> adminLogin(@RequestBody LoginDto dto) {
        return Result.success(authService.adminLogin(dto));
    }

    @Operation(summary = "顾客登录", description = "普通用户使用用户名和密码进行登录，返回JWT令牌")
    @PostMapping("/customer/login")
    public Result<LoginVo> customerLogin(@RequestBody LoginDto dto) {
        return Result.success(authService.customerLogin(dto));
    }

    @Operation(summary = "商家登录", description = "商家使用用户名和密码进行登录，返回JWT令牌")
    @PostMapping("/merchant/login")
    public Result<LoginVo> merchantLogin(@RequestBody LoginDto dto) {
        return Result.success(authService.merchantLogin(dto));
    }

    @Operation(summary = "骑手登录", description = "配送骑手使用用户名和密码进行登录，返回JWT令牌")
    @PostMapping("/rider/login")
    public Result<LoginVo> riderLogin(@RequestBody LoginDto dto) {
        return Result.success(authService.riderLogin(dto));
    }

    @Operation(summary = "顾客注册", description = "新用户注册账号，需要提供用户名、密码和手机号")
    @PostMapping("/customer/register")
    public Result<Void> customerRegister(@RequestBody RegisterDto dto) {
        authService.customerRegister(dto);
        return Result.success(null);
    }
}
