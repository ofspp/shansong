package com.delivery.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.delivery.common.BusinessException;
import com.delivery.entity.Orders;
import com.delivery.entity.Rider;
import com.delivery.mapper.OrdersMapper;
import com.delivery.mapper.RiderMapper;
import com.delivery.security.JwtUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RiderService {

    private final RiderMapper riderMapper;
    private final OrdersMapper ordersMapper;

    private Long getCurrentRiderId() {
        JwtUserDetails userDetails = (JwtUserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return userDetails.getUserId();
    }

    public Rider getRiderInfo() {
        return riderMapper.selectById(getCurrentRiderId());
    }

    public void updateWorkingStatus(Integer status) {
        Rider rider = riderMapper.selectById(getCurrentRiderId());
        rider.setWorkingStatus(status);
        riderMapper.updateById(rider);
    }

    public IPage<Orders> getAvailableOrders(int page, int size) {
        Page<Orders> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Orders::getStatus, "PICKING");
        wrapper.isNull(Orders::getRiderId);
        wrapper.orderByAsc(Orders::getCreateTime);
        return ordersMapper.selectPage(pageParam, wrapper);
    }

    public IPage<Orders> getMyOrders(int page, int size, String status) {
        Page<Orders> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Orders::getRiderId, getCurrentRiderId());
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Orders::getStatus, status);
        }
        wrapper.orderByDesc(Orders::getCreateTime);
        return ordersMapper.selectPage(pageParam, wrapper);
    }

    public void grabOrder(Long orderId) {
        Long riderId = getCurrentRiderId();
        Rider rider = riderMapper.selectById(riderId);

        if (rider.getWorkingStatus() == null || rider.getWorkingStatus() != 1) {
            throw new BusinessException("请先切换到接单状态");
        }

        Orders order = ordersMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!"PICKING".equals(order.getStatus())) {
            throw new BusinessException("该订单不可抢");
        }
        if (order.getRiderId() != null) {
            throw new BusinessException("该订单已被抢走");
        }

        order.setRiderId(riderId);
        order.setStatus("DELIVERING");
        order.setGrabTime(LocalDateTime.now());
        ordersMapper.updateById(order);
    }

    public void updateDeliveryStatus(Long orderId, String status) {
        Orders order = ordersMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getRiderId().equals(getCurrentRiderId())) {
            throw new BusinessException("无权操作此订单");
        }
        if ("COMPLETED".equals(status)) {
            order.setStatus("COMPLETED");
            order.setCompleteTime(LocalDateTime.now());
        } else {
            order.setStatus(status);
        }
        ordersMapper.updateById(order);
    }
}
