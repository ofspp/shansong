package com.delivery.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.delivery.common.Result;
import com.delivery.entity.*;
import com.delivery.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
@Tag(name = "顾客模块", description = "顾客端功能：浏览商家、购物车、订单管理等")
public class CustomerController {

    private final CustomerService customerService;

    @Operation(summary = "获取商家列表", description = "分页查询商家信息，支持按店铺名称搜索")
    @GetMapping("/merchants")
    public Result<IPage<Merchant>> getMerchantList(
            @Parameter(description = "页码，默认1") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量，默认10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "搜索关键词（店铺名称）") @RequestParam(required = false) String keyword) {
        return Result.success(customerService.getMerchantList(page, size, keyword));
    }

    @Operation(summary = "获取商家详情", description = "根据商家ID获取详细信息，包括营业时间、配送费等")
    @GetMapping("/merchant/{id}")
    public Result<Merchant> getMerchantDetail(
            @Parameter(description = "商家ID") @PathVariable Long id) {
        return Result.success(customerService.getMerchantDetail(id));
    }

    @Operation(summary = "获取菜品分类", description = "获取指定商家的所有菜品分类")
    @GetMapping("/merchant/{merchantId}/categories")
    public Result<List<Category>> getCategories(
            @Parameter(description = "商家ID") @PathVariable Long merchantId) {
        return Result.success(customerService.getCategories(merchantId));
    }

    @Operation(summary = "获取菜品列表", description = "分页查询指定商家的菜品，可按分类筛选")
    @GetMapping("/merchant/{merchantId}/dishes")
    public Result<IPage<Dish>> getDishList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "商家ID") @PathVariable Long merchantId,
            @Parameter(description = "分类ID（可选）") @RequestParam(required = false) Long categoryId) {
        return Result.success(customerService.getDishList(page, size, merchantId, categoryId));
    }

    @Operation(summary = "添加到购物车", description = "将菜品加入购物车，如果已存在则增加数量")
    @PostMapping("/cart")
    public Result<Void> addToCart(
            @Parameter(description = "菜品ID") @RequestParam Long dishId,
            @Parameter(description = "购买数量，默认1") @RequestParam(defaultValue = "1") Integer quantity) {
        customerService.addToCart(dishId, quantity);
        return Result.success(null);
    }

    @Operation(summary = "获取购物车列表", description = "获取当前用户的购物车中所有商品")
    @GetMapping("/cart")
    public Result<List<Cart>> getCartList() {
        return Result.success(customerService.getCartList());
    }

    @Operation(summary = "修改购物车商品数量", description = "更新购物车中指定商品的数量")
    @PutMapping("/cart/{cartId}")
    public Result<Void> updateCartItem(
            @Parameter(description = "购物车项ID") @PathVariable Long cartId,
            @Parameter(description = "新的数量") @RequestParam Integer quantity) {
        customerService.updateCartItem(cartId, quantity);
        return Result.success(null);
    }

    @Operation(summary = "删除购物车商品", description = "从购物车中移除指定商品")
    @DeleteMapping("/cart/{cartId}")
    public Result<Void> deleteCartItem(
            @Parameter(description = "购物车项ID") @PathVariable Long cartId) {
        customerService.deleteCartItem(cartId);
        return Result.success(null);
    }

    @Operation(summary = "清空购物车", description = "清空当前用户购物车中的所有商品")
    @DeleteMapping("/cart")
    public Result<Void> clearCart() {
        customerService.clearCart();
        return Result.success(null);
    }

    @Operation(summary = "获取收货地址列表", description = "获取当前用户的所有收货地址")
    @GetMapping("/addresses")
    public Result<List<CustomerAddress>> getAddressList() {
        return Result.success(customerService.getAddressList());
    }

    @Operation(summary = "添加收货地址", description = "新增一个收货地址")
    @PostMapping("/address")
    public Result<CustomerAddress> addAddress(@RequestBody CustomerAddress address) {
        return Result.success(customerService.addAddress(address));
    }

    @Operation(summary = "修改收货地址", description = "更新指定收货地址信息")
    @PutMapping("/address")
    public Result<CustomerAddress> updateAddress(@RequestBody CustomerAddress address) {
        return Result.success(customerService.updateAddress(address));
    }

    @Operation(summary = "删除收货地址", description = "删除指定收货地址")
    @DeleteMapping("/address/{addressId}")
    public Result<Void> deleteAddress(
            @Parameter(description = "地址ID") @PathVariable Long addressId) {
        customerService.deleteAddress(addressId);
        return Result.success(null);
    }

    @Operation(summary = "设置默认地址", description = "将指定地址设为默认收货地址")
    @PutMapping("/address/{addressId}/default")
    public Result<Void> setDefaultAddress(
            @Parameter(description = "地址ID") @PathVariable Long addressId) {
        customerService.setDefaultAddress(addressId);
        return Result.success(null);
    }

    @Operation(summary = "创建订单", description = "基于购物车内容创建新订单，需要选择收货地址")
    @PostMapping("/order")
    public Result<Long> createOrder(
            @Parameter(description = "收货地址ID") @RequestParam Long addressId,
            @Parameter(description = "订单备注") @RequestParam(required = false) String remark) {
        return Result.success(customerService.createOrder(addressId, remark));
    }

    @Operation(summary = "获取我的订单列表", description = "分页查询当前用户的订单记录，支持按状态筛选")
    @GetMapping("/orders")
    public Result<IPage<Orders>> getOrderList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "订单状态筛选：PENDING_PAYMENT/PAID/PENDING_ACCEPT/ACCEPTED/PICKING/DELIVERING/COMPLETED/CANCELLED") @RequestParam(required = false) String status) {
        return Result.success(customerService.getOrderList(page, size, status));
    }

    @Operation(summary = "获取订单详情", description = "查看订单的完整信息，包括配送地址、商品明细等")
    @GetMapping("/order/{orderId}")
    public Result<Orders> getOrderDetail(
            @Parameter(description = "订单ID") @PathVariable Long orderId) {
        return Result.success(customerService.getOrderDetail(orderId));
    }

    @Operation(summary = "模拟支付", description = "输入支付金额完成模拟支付，金额必须与订单实付金额一致")
    @PutMapping("/order/{orderId}/pay")
    public Result<Void> payOrder(
            @Parameter(description = "订单ID") @PathVariable Long orderId,
            @Parameter(description = "支付金额") @RequestParam BigDecimal amount) {
        customerService.payOrder(orderId, amount);
        return Result.success(null);
    }

    @Operation(summary = "获取订单商品明细", description = "获取订单中包含的所有商品详情")
    @GetMapping("/order/{orderId}/items")
    public Result<List<OrderItem>> getOrderItems(
            @Parameter(description = "订单ID") @PathVariable Long orderId) {
        return Result.success(customerService.getOrderItems(orderId));
    }

    @Operation(summary = "取消订单", description = "取消待支付的订单，其他状态无法取消")
    @PutMapping("/order/{orderId}/cancel")
    public Result<Void> cancelOrder(
            @Parameter(description = "订单ID") @PathVariable Long orderId) {
        customerService.cancelOrder(orderId);
        return Result.success(null);
    }

    @Operation(summary = "确认收货", description = "确认收到配送完成的订单")
    @PutMapping("/order/{orderId}/confirm")
    public Result<Void> confirmOrder(
            @Parameter(description = "订单ID") @PathVariable Long orderId) {
        customerService.confirmOrder(orderId);
        return Result.success(null);
    }

    // ===== 点赞推荐系统 =====

    @Operation(summary = "点赞/取消点赞商家", description = "切换当前用户对商家的点赞状态")
    @PostMapping("/merchant/{merchantId}/like")
    public Result<Map<String, Object>> toggleLike(
            @Parameter(description = "商家ID") @PathVariable Long merchantId) {
        boolean liked = customerService.toggleLike(merchantId);
        Merchant merchant = customerService.getMerchantDetail(merchantId);
        return Result.success(Map.of("liked", liked, "likesCount", merchant.getLikesCount() != null ? merchant.getLikesCount() : 0));
    }

    @Operation(summary = "获取当前用户点赞的商家ID列表")
    @GetMapping("/liked-merchants")
    public Result<List<Long>> getLikedMerchantIds() {
        return Result.success(customerService.getLikedMerchantIds());
    }

    @Operation(summary = "获取推荐商家", description = "根据点赞数排序推荐商家")
    @GetMapping("/recommended-merchants")
    public Result<List<Merchant>> getRecommendedMerchants(
            @Parameter(description = "推荐数量") @RequestParam(defaultValue = "6") int limit) {
        return Result.success(customerService.getRecommendedMerchants(limit));
    }
}
