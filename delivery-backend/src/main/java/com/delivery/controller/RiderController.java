package com.delivery.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.delivery.common.Result;
import com.delivery.entity.Orders;
import com.delivery.entity.Rider;
import com.delivery.service.RiderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rider")
@RequiredArgsConstructor
@Tag(name = "骑手模块", description = "骑手端功能：抢单配送、工作状态管理等")
public class RiderController {

    private final RiderService riderService;

    @Operation(summary = "获取个人信息", description = "获取当前登录骑手的个人资料和工作状态")
    @GetMapping("/info")
    public Result<Rider> getRiderInfo() {
        return Result.success(riderService.getRiderInfo());
    }

    @Operation(summary = "切换工作状态", description = "切换接单/休息状态（1=接单中，0=休息中）")
    @PutMapping("/working-status")
    public Result<Void> updateWorkingStatus(
            @Parameter(description = "工作状态：1-接单中，0-休息中") @RequestParam Integer status) {
        riderService.updateWorkingStatus(status);
        return Result.success(null);
    }

    @Operation(summary = "获取可抢订单列表", description = "查询当前可以抢取的待配送订单（商家已备餐完成）")
    @GetMapping("/available-orders")
    public Result<IPage<Orders>> getAvailableOrders(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int size) {
        return Result.success(riderService.getAvailableOrders(page, size));
    }

    @Operation(summary = "获取我的订单列表", description = "查询自己已接的订单，支持按状态筛选")
    @GetMapping("/my-orders")
    public Result<IPage<Orders>> getMyOrders(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "订单状态筛选：DELIVERING/COMPLETED") @RequestParam(required = false) String status) {
        return Result.success(riderService.getMyOrders(page, size, status));
    }

    @Operation(summary = "抢单", description = "骑手抢取待配送订单，先到先得")
    @PostMapping("/order/{orderId}/grab")
    public Result<Void> grabOrder(
            @Parameter(description = "订单ID") @PathVariable Long orderId) {
        riderService.grabOrder(orderId);
        return Result.success(null);
    }

    @Operation(summary = "更新配送状态", description = "更新订单配送状态（如标记为已完成送达）")
    @PutMapping("/order/{orderId}/status")
    public Result<Void> updateDeliveryStatus(
            @Parameter(description = "订单ID") @PathVariable Long orderId,
            @Parameter(description = "目标状态：COMPLETED-已完成") @RequestParam String status) {
        riderService.updateDeliveryStatus(orderId, status);
        return Result.success(null);
    }
}
