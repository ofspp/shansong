package com.delivery.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.delivery.common.Result;
import com.delivery.entity.*;
import com.delivery.service.MerchantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/merchant")
@RequiredArgsConstructor
@Tag(name = "商家模块", description = "商家端功能：店铺管理、菜品管理、订单处理等")
public class MerchantController {

    private final MerchantService merchantService;

    @Operation(summary = "获取店铺信息", description = "获取当前登录商家的店铺详细信息")
    @GetMapping("/info")
    public Result<Merchant> getMerchantInfo() {
        return Result.success(merchantService.getMerchantInfo());
    }

    @Operation(summary = "更新店铺信息", description = "修改店铺名称、电话、营业时间、配送费等信息")
    @PutMapping("/info")
    public Result<Void> updateMerchantInfo(@RequestBody Merchant merchant) {
        merchantService.updateMerchantInfo(merchant);
        return Result.success(null);
    }

    @Operation(summary = "获取菜品分类列表", description = "获取当前店铺的所有菜品分类，按排序号升序排列")
    @GetMapping("/categories")
    public Result<List<Category>> getCategories() {
        return Result.success(merchantService.getCategories());
    }

    @Operation(summary = "添加菜品分类", description = "创建新的菜品分类，如热销推荐、主食、饮品等")
    @PostMapping("/category")
    public Result<Void> addCategory(@RequestBody Category category) {
        merchantService.addCategory(category);
        return Result.success(null);
    }

    @Operation(summary = "修改菜品分类", description = "更新已有分类的名称和排序顺序")
    @PutMapping("/category")
    public Result<Void> updateCategory(@RequestBody Category category) {
        merchantService.updateCategory(category);
        return Result.success(null);
    }

    @Operation(summary = "删除菜品分类", description = "删除指定分类（该分类下不能有菜品）")
    @DeleteMapping("/category/{categoryId}")
    public Result<Void> deleteCategory(
            @Parameter(description = "分类ID") @PathVariable Long categoryId) {
        merchantService.deleteCategory(categoryId);
        return Result.success(null);
    }

    @Operation(summary = "获取菜品列表", description = "分页查询当前店铺的菜品，可按分类筛选")
    @GetMapping("/dishes")
    public Result<IPage<Dish>> getDishList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "分类ID（可选）") @RequestParam(required = false) Long categoryId) {
        return Result.success(merchantService.getDishList(page, size, categoryId));
    }

    @Operation(summary = "添加菜品", description = "创建新菜品，包括名称、价格、图片、所属分类等")
    @PostMapping("/dish")
    public Result<Void> addDish(@RequestBody Dish dish) {
        merchantService.addDish(dish);
        return Result.success(null);
    }

    @Operation(summary = "修改菜品", description = "更新菜品的名称、价格、描述、图片等信息")
    @PutMapping("/dish")
    public Result<Void> updateDish(@RequestBody Dish dish) {
        merchantService.updateDish(dish);
        return Result.success(null);
    }

    @Operation(summary = "上架/下架菜品", description = "切换菜品的上下架状态（1=上架，0=下架）")
    @PutMapping("/dish/{dishId}/status")
    public Result<Void> updateDishStatus(
            @Parameter(description = "菜品ID") @PathVariable Long dishId,
            @Parameter(description = "状态：1-上架，0-下架") @RequestParam Integer status) {
        merchantService.updateDishStatus(dishId, status);
        return Result.success(null);
    }

    @Operation(summary = "删除菜品", description = "从系统中删除指定菜品")
    @DeleteMapping("/dish/{dishId}")
    public Result<Void> deleteDish(
            @Parameter(description = "菜品ID") @PathVariable Long dishId) {
        merchantService.deleteDish(dishId);
        return Result.success(null);
    }

    @Operation(summary = "获取订单列表", description = "分页查询本店的订单记录，支持按状态筛选")
    @GetMapping("/orders")
    public Result<IPage<Orders>> getOrderList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "订单状态筛选") @RequestParam(required = false) String status) {
        return Result.success(merchantService.getOrderList(page, size, status));
    }

    @Operation(summary = "获取订单详情", description = "查看订单完整信息和商品明细")
    @GetMapping("/order/{orderId}")
    public Result<Orders> getOrderDetail(
            @Parameter(description = "订单ID") @PathVariable Long orderId) {
        return Result.success(merchantService.getOrderDetail(orderId));
    }

    @Operation(summary = "获取订单商品明细", description = "获取订单中包含的所有商品详情")
    @GetMapping("/order/{orderId}/items")
    public Result<List<OrderItem>> getOrderItems(
            @Parameter(description = "订单ID") @PathVariable Long orderId) {
        return Result.success(merchantService.getOrderItems(orderId));
    }

    @Operation(summary = "接单", description = "商家接受新订单，将状态改为已接单")
    @PutMapping("/order/{orderId}/accept")
    public Result<Void> acceptOrder(
            @Parameter(description = "订单ID") @PathVariable Long orderId) {
        merchantService.acceptOrder(orderId);
        return Result.success(null);
    }

    @Operation(summary = "拒单", description = "商家拒绝新订单，需填写拒绝原因")
    @PutMapping("/order/{orderId}/reject")
    public Result<Void> rejectOrder(
            @Parameter(description = "订单ID") @PathVariable Long orderId,
            @Parameter(description = "拒绝原因") @RequestParam(required = false) String reason) {
        merchantService.rejectOrder(orderId, reason);
        return Result.success(null);
    }

    @Operation(summary = "开始备餐", description = "确认接单后开始准备餐品，骑手可以看到此订单")
    @PutMapping("/order/{orderId}/prepare")
    public Result<Void> startPreparing(
            @Parameter(description = "订单ID") @PathVariable Long orderId) {
        merchantService.startPreparing(orderId);
        return Result.success(null);
    }
}
