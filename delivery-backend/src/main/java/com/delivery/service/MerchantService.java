package com.delivery.service;

import com.delivery.entity.Category;
import com.delivery.entity.Dish;
import com.delivery.entity.Merchant;
import com.delivery.entity.OrderItem;
import com.delivery.entity.Orders;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

public interface MerchantService {

    Merchant getMerchantInfo();

    void updateMerchantInfo(Merchant merchant);

    List<Category> getCategories();

    void addCategory(Category category);

    void updateCategory(Category category);

    void deleteCategory(Long categoryId);

    IPage<Dish> getDishList(int page, int size, Long categoryId);

    void addDish(Dish dish);

    void updateDish(Dish dish);

    void updateDishStatus(Long dishId, Integer status);

    void deleteDish(Long dishId);

    IPage<Orders> getOrderList(int page, int size, String status);

    Orders getOrderDetail(Long orderId);

    void acceptOrder(Long orderId);

    void rejectOrder(Long orderId, String reason);

    void startPreparing(Long orderId);

    List<OrderItem> getOrderItems(Long orderId);
}
