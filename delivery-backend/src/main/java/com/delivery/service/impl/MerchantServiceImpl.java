package com.delivery.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.delivery.common.BusinessException;
import com.delivery.entity.*;
import com.delivery.mapper.*;
import com.delivery.security.JwtUserDetails;
import com.delivery.service.MerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MerchantServiceImpl implements MerchantService {

    private final MerchantMapper merchantMapper;
    private final CategoryMapper categoryMapper;
    private final DishMapper dishMapper;
    private final OrdersMapper ordersMapper;
    private final OrderItemMapper orderItemMapper;

    private Long getCurrentMerchantId() {
        JwtUserDetails userDetails = (JwtUserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return userDetails.getUserId();
    }

    public Merchant getMerchantInfo() {
        return merchantMapper.selectById(getCurrentMerchantId());
    }

    public void updateMerchantInfo(Merchant merchant) {
        Long id = getCurrentMerchantId();
        merchant.setId(id);
        merchantMapper.updateById(merchant);
    }

    public List<Category> getCategories() {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getMerchantId, getCurrentMerchantId());
        wrapper.orderByAsc(Category::getSort);
        return categoryMapper.selectList(wrapper);
    }

    public void addCategory(Category category) {
        category.setMerchantId(getCurrentMerchantId());
        categoryMapper.insert(category);
    }

    public void updateCategory(Category category) {
        category.setMerchantId(getCurrentMerchantId());
        categoryMapper.updateById(category);
    }

    public void deleteCategory(Long categoryId) {
        long count = dishMapper.selectCount(
                new LambdaQueryWrapper<Dish>().eq(Dish::getCategoryId, categoryId)
        );
        if (count > 0) {
            throw new BusinessException("该分类下存在菜品，无法删除");
        }
        categoryMapper.deleteById(categoryId);
    }

    public IPage<Dish> getDishList(int page, int size, Long categoryId) {
        Page<Dish> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dish::getMerchantId, getCurrentMerchantId());
        if (categoryId != null) {
            wrapper.eq(Dish::getCategoryId, categoryId);
        }
        wrapper.orderByDesc(Dish::getSort);
        return dishMapper.selectPage(pageParam, wrapper);
    }

    public void addDish(Dish dish) {
        dish.setMerchantId(getCurrentMerchantId());
        dish.setStatus(1);
        dishMapper.insert(dish);
    }

    public void updateDish(Dish dish) {
        dish.setMerchantId(getCurrentMerchantId());
        dishMapper.updateById(dish);
    }

    public void updateDishStatus(Long dishId, Integer status) {
        Dish dish = dishMapper.selectById(dishId);
        if (dish == null || !dish.getMerchantId().equals(getCurrentMerchantId())) {
            throw new BusinessException("菜品不存在");
        }
        dish.setStatus(status);
        dishMapper.updateById(dish);
    }

    public void deleteDish(Long dishId) {
        dishMapper.deleteById(dishId);
    }

    public IPage<Orders> getOrderList(int page, int size, String status) {
        Page<Orders> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Orders::getMerchantId, getCurrentMerchantId());
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Orders::getStatus, status);
        }
        wrapper.orderByDesc(Orders::getCreateTime);
        return ordersMapper.selectPage(pageParam, wrapper);
    }

    public Orders getOrderDetail(Long orderId) {
        return ordersMapper.selectById(orderId);
    }

    public List<OrderItem> getOrderItems(Long orderId) {
        return orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId)
        );
    }

    public void acceptOrder(Long orderId) {
        Orders order = ordersMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!"PAID".equals(order.getStatus()) && !"PENDING_ACCEPT".equals(order.getStatus())) {
            throw new BusinessException("当前状态无法接单");
        }
        order.setStatus("ACCEPTED");
        order.setAcceptTime(java.time.LocalDateTime.now());
        ordersMapper.updateById(order);
    }

    public void rejectOrder(Long orderId, String reason) {
        Orders order = ordersMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!"PENDING_ACCEPT".equals(order.getStatus()) && !"PAID".equals(order.getStatus())) {
            throw new BusinessException("当前状态无法拒单");
        }
        order.setStatus("CANCELLED");
        order.setCancelReason(reason != null ? reason : "商家拒单");
        ordersMapper.updateById(order);
    }

    public void startPreparing(Long orderId) {
        Orders order = ordersMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!"ACCEPTED".equals(order.getStatus())) {
            throw new BusinessException("当前状态无法开始备餐");
        }
        order.setStatus("PICKING");
        ordersMapper.updateById(order);
    }
}
