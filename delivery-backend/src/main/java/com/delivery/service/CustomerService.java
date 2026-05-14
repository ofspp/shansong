package com.delivery.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.delivery.common.BusinessException;
import com.delivery.entity.*;
import com.delivery.mapper.*;
import com.delivery.security.JwtUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final MerchantMapper merchantMapper;
    private final DishMapper dishMapper;
    private final CategoryMapper categoryMapper;
    private final CartMapper cartMapper;
    private final OrdersMapper ordersMapper;
    private final CustomerAddressMapper customerAddressMapper;
    private final OrderItemMapper orderItemMapper;
    private final MerchantLikeMapper merchantLikeMapper;

    private Long getCurrentUserId() {
        JwtUserDetails userDetails = (JwtUserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return userDetails.getUserId();
    }

    public IPage<Merchant> getMerchantList(int page, int size, String keyword) {
        Page<Merchant> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Merchant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Merchant::getStatus, 1);
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Merchant::getShopName, keyword);
        }
        wrapper.orderByDesc(Merchant::getCreateTime);
        return merchantMapper.selectPage(pageParam, wrapper);
    }

    public Merchant getMerchantDetail(Long id) {
        return merchantMapper.selectById(id);
    }

    public List<Category> getCategories(Long merchantId) {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getMerchantId, merchantId);
        wrapper.orderByAsc(Category::getSort);
        return categoryMapper.selectList(wrapper);
    }

    public IPage<Dish> getDishList(int page, int size, Long merchantId, Long categoryId) {
        Page<Dish> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dish::getMerchantId, merchantId);
        wrapper.eq(Dish::getStatus, 1);
        if (categoryId != null) {
            wrapper.eq(Dish::getCategoryId, categoryId);
        }
        wrapper.orderByDesc(Dish::getSort);
        return dishMapper.selectPage(pageParam, wrapper);
    }

    public void addToCart(Long dishId, Integer quantity) {
        Long customerId = getCurrentUserId();

        Cart existing = cartMapper.selectOne(
                new LambdaQueryWrapper<Cart>()
                        .eq(Cart::getCustomerId, customerId)
                        .eq(Cart::getDishId, dishId)
        );

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + quantity);
            cartMapper.updateById(existing);
        } else {
            Cart cart = new Cart();
            cart.setCustomerId(customerId);
            cart.setMerchantId(dishMapper.selectById(dishId).getMerchantId());
            cart.setDishId(dishId);
            cart.setQuantity(quantity);
            cartMapper.insert(cart);
        }
    }

    public List<Cart> getCartList() {
        Long customerId = getCurrentUserId();
        return cartMapper.selectList(
                new LambdaQueryWrapper<Cart>().eq(Cart::getCustomerId, customerId)
        );
    }

    public void updateCartItem(Long cartId, Integer quantity) {
        Cart cart = cartMapper.selectById(cartId);
        if (cart == null) {
            throw new BusinessException("购物车项不存在");
        }
        cart.setQuantity(quantity);
        cartMapper.updateById(cart);
    }

    public void deleteCartItem(Long cartId) {
        cartMapper.deleteById(cartId);
    }

    public void clearCart() {
        Long customerId = getCurrentUserId();
        cartMapper.delete(new LambdaQueryWrapper<Cart>().eq(Cart::getCustomerId, customerId));
    }

    public List<CustomerAddress> getAddressList() {
        Long customerId = getCurrentUserId();
        return customerAddressMapper.selectList(
                new LambdaQueryWrapper<CustomerAddress>()
                        .eq(CustomerAddress::getCustomerId, customerId)
                        .orderByDesc(CustomerAddress::getIsDefault)
        );
    }

    public CustomerAddress addAddress(CustomerAddress address) {
        Long customerId = getCurrentUserId();
        address.setCustomerId(customerId);
        // 如果设为默认地址，先取消其他默认
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            clearDefaultAddress(customerId);
        }
        customerAddressMapper.insert(address);
        return address;
    }

    public CustomerAddress updateAddress(CustomerAddress address) {
        Long customerId = getCurrentUserId();
        CustomerAddress existing = customerAddressMapper.selectById(address.getId());
        if (existing == null || !existing.getCustomerId().equals(customerId)) {
            throw new BusinessException("地址不存在");
        }
        address.setCustomerId(customerId);
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            clearDefaultAddress(customerId);
        }
        customerAddressMapper.updateById(address);
        return address;
    }

    public void deleteAddress(Long addressId) {
        Long customerId = getCurrentUserId();
        CustomerAddress existing = customerAddressMapper.selectById(addressId);
        if (existing == null || !existing.getCustomerId().equals(customerId)) {
            throw new BusinessException("地址不存在");
        }
        customerAddressMapper.deleteById(addressId);
    }

    public void setDefaultAddress(Long addressId) {
        Long customerId = getCurrentUserId();
        CustomerAddress existing = customerAddressMapper.selectById(addressId);
        if (existing == null || !existing.getCustomerId().equals(customerId)) {
            throw new BusinessException("地址不存在");
        }
        clearDefaultAddress(customerId);
        existing.setIsDefault(1);
        customerAddressMapper.updateById(existing);
    }

    private void clearDefaultAddress(Long customerId) {
        List<CustomerAddress> defaults = customerAddressMapper.selectList(
                new LambdaQueryWrapper<CustomerAddress>()
                        .eq(CustomerAddress::getCustomerId, customerId)
                        .eq(CustomerAddress::getIsDefault, 1)
        );
        for (CustomerAddress addr : defaults) {
            addr.setIsDefault(0);
            customerAddressMapper.updateById(addr);
        }
    }

    public Long createOrder(Long addressId, String remark) {
        Long customerId = getCurrentUserId();

        List<Cart> cartItems = cartMapper.selectList(
                new LambdaQueryWrapper<Cart>().eq(Cart::getCustomerId, customerId)
        );
        if (cartItems == null || cartItems.isEmpty()) {
            throw new BusinessException("购物车为空");
        }

        // 获取收货地址信息
        CustomerAddress address = customerAddressMapper.selectById(addressId);
        if (address == null) {
            throw new BusinessException("收货地址不存在");
        }

        Orders order = new Orders();
        order.setOrderNo(generateOrderNo());
        order.setCustomerId(customerId);
        order.setStatus("PENDING_PAYMENT");
        order.setRemark(remark);
        order.setAddressId(addressId);
        order.setReceiverName(address.getReceiverName());
        order.setReceiverPhone(address.getReceiverPhone());
        order.setProvince(address.getProvince());
        order.setCity(address.getCity());
        order.setDistrict(address.getDistrict());
        order.setDetailAddress(address.getDetailAddress());

        BigDecimal totalAmount = BigDecimal.ZERO;
        Long merchantId = null;

        for (Cart cart : cartItems) {
            Dish dish = dishMapper.selectById(cart.getDishId());
            if (dish == null) continue;

            BigDecimal subtotal = dish.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity()));
            totalAmount = totalAmount.add(subtotal);
            if (merchantId == null) {
                merchantId = dish.getMerchantId();
            }
        }

        Merchant merchant = merchantMapper.selectById(merchantId);
        BigDecimal deliveryFee = merchant != null ? merchant.getDeliveryFee() : BigDecimal.ZERO;

        order.setMerchantId(merchantId);
        order.setTotalAmount(totalAmount);
        order.setDeliveryFee(deliveryFee);
        order.setPayAmount(totalAmount.add(deliveryFee));
        order.setActualAmount(totalAmount.add(deliveryFee));
        ordersMapper.insert(order);

        for (Cart cart : cartItems) {
            Dish dish = dishMapper.selectById(cart.getDishId());
            if (dish == null) continue;

            OrderItem item = new OrderItem();
            item.setOrderId(order.getId());
            item.setDishId(dish.getId());
            item.setDishName(dish.getName());
            item.setDishImage(dish.getImage());
            item.setPrice(dish.getPrice());
            item.setQuantity(cart.getQuantity());
            item.setSubtotal(dish.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())));
            orderItemMapper.insert(item);
        }

        clearCart();
        return order.getId();
    }

    private String generateOrderNo() {
        return "DD" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + String.format("%03d", (int)(Math.random() * 1000));
    }

    public IPage<Orders> getOrderList(int page, int size, String status) {
        Long customerId = getCurrentUserId();
        Page<Orders> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Orders::getCustomerId, customerId);
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Orders::getStatus, status);
        }
        wrapper.orderByDesc(Orders::getCreateTime);
        return ordersMapper.selectPage(pageParam, wrapper);
    }

    public Orders getOrderDetail(Long orderId) {
        return ordersMapper.selectById(orderId);
    }

    public void cancelOrder(Long orderId) {
        Orders order = ordersMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!"PENDING_PAYMENT".equals(order.getStatus())) {
            throw new BusinessException("当前状态无法取消");
        }
        order.setStatus("CANCELLED");
        order.setCancelReason("用户取消");
        ordersMapper.updateById(order);
    }

    public void payOrder(Long orderId, BigDecimal amount) {
        Orders order = ordersMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!"PENDING_PAYMENT".equals(order.getStatus())) {
            throw new BusinessException("订单状态异常，无法支付");
        }
        if (order.getActualAmount().compareTo(amount) != 0) {
            throw new BusinessException("支付金额不正确，应付金额为" + order.getActualAmount());
        }
        order.setStatus("PENDING_ACCEPT");
        order.setPayMethod("SIMULATED");
        order.setPayTime(LocalDateTime.now());
        ordersMapper.updateById(order);
    }

    public List<OrderItem> getOrderItems(Long orderId) {
        return orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId)
        );
    }

    public void confirmOrder(Long orderId) {
        Orders order = ordersMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!"DELIVERING".equals(order.getStatus())) {
            throw new BusinessException("当前状态无法确认收货");
        }
        order.setStatus("COMPLETED");
        order.setCompleteTime(LocalDateTime.now());
        ordersMapper.updateById(order);
    }

    // ===== 点赞推荐系统 =====

    public boolean toggleLike(Long merchantId) {
        Long customerId = getCurrentUserId();
        MerchantLike existing = merchantLikeMapper.selectOne(
                new LambdaQueryWrapper<MerchantLike>()
                        .eq(MerchantLike::getCustomerId, customerId)
                        .eq(MerchantLike::getMerchantId, merchantId)
        );
        Merchant merchant = merchantMapper.selectById(merchantId);
        if (merchant == null) {
            throw new BusinessException("商家不存在");
        }
        if (existing != null) {
            // 取消点赞
            merchantLikeMapper.deleteById(existing.getId());
            merchant.setLikesCount(Math.max(0, (merchant.getLikesCount() != null ? merchant.getLikesCount() : 0) - 1));
            merchantMapper.updateById(merchant);
            return false;
        } else {
            // 点赞
            MerchantLike like = new MerchantLike();
            like.setCustomerId(customerId);
            like.setMerchantId(merchantId);
            merchantLikeMapper.insert(like);
            merchant.setLikesCount((merchant.getLikesCount() != null ? merchant.getLikesCount() : 0) + 1);
            merchantMapper.updateById(merchant);
            return true;
        }
    }

    public boolean isLiked(Long merchantId) {
        Long customerId = getCurrentUserId();
        Long count = merchantLikeMapper.selectCount(
                new LambdaQueryWrapper<MerchantLike>()
                        .eq(MerchantLike::getCustomerId, customerId)
                        .eq(MerchantLike::getMerchantId, merchantId)
        );
        return count > 0;
    }

    public List<Long> getLikedMerchantIds() {
        Long customerId = getCurrentUserId();
        List<MerchantLike> likes = merchantLikeMapper.selectList(
                new LambdaQueryWrapper<MerchantLike>().eq(MerchantLike::getCustomerId, customerId)
        );
        return likes.stream().map(MerchantLike::getMerchantId).toList();
    }

    public List<Merchant> getRecommendedMerchants(int limit) {
        LambdaQueryWrapper<Merchant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Merchant::getStatus, 1);
        wrapper.orderByDesc(Merchant::getLikesCount);
        wrapper.last("LIMIT " + limit);
        return merchantMapper.selectList(wrapper);
    }
}
