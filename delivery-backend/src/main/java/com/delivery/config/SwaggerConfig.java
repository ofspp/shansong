package com.delivery.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("外卖配送平台 API 接口文档")
                        .description("""
                                ## 项目简介
                                基于Spring Boot + Vue的外卖配送平台系统，支持多角色（管理员、商家、骑手、用户）操作。
                                
                                ## 系统架构
                                - **后端框架**：Spring Boot 3.2.0 + MyBatis-Plus 3.5.7
                                - **安全认证**：JWT Token + Spring Security
                                - **数据库**：MySQL 8.0
                                
                                ## 角色说明
                                | 角色 | 说明 |
                                |------|------|
                                | ADMIN | 系统管理员，管理平台运营 |
                                | MERCHANT | 商家，管理店铺和订单 |
                                | RIDER | 骑手，接单和配送 |
                                | CUSTOMER | 用户，浏览商品和下单 |
                                
                                ## 认证方式
                                除登录/注册接口外，所有接口需要在请求头中携带JWT Token：
                                `Authorization: Bearer <token>`
                                
                                ## 测试账号
                                - 管理员：admin / 123456
                                - 商家：merchant1 / 123456
                                - 骑手：rider1 / 123456
                                - 用户：customer1 / 123456
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("何翔宇")
                                .email("example@email.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer"))
                .components(new Components()
                        .addSecuritySchemes("Bearer",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT认证令牌")));
    }
}
