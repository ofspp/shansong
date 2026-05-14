package com.delivery.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.delivery.common.BusinessException;
import com.delivery.entity.Evaluation;
import com.delivery.entity.Orders;
import com.delivery.mapper.EvaluationMapper;
import com.delivery.mapper.OrdersMapper;
import com.delivery.security.JwtUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EvaluationService {

    private final EvaluationMapper evaluationMapper;
    private final OrdersMapper ordersMapper;

    private Long getCurrentUserId() {
        JwtUserDetails userDetails = (JwtUserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return userDetails.getUserId();
    }

    public void createEvaluation(Long orderId, Integer rating, String content) {
        Long customerId = getCurrentUserId();

        Orders order = ordersMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getCustomerId().equals(customerId)) {
            throw new BusinessException("无权评价此订单");
        }
        if (!"COMPLETED".equals(order.getStatus())) {
            throw new BusinessException("只有已完成的订单才能评价");
        }

        Evaluation existEval = evaluationMapper.selectOne(
                new LambdaQueryWrapper<Evaluation>().eq(Evaluation::getOrderId, orderId)
        );
        if (existEval != null) {
            throw new BusinessException("该订单已评价");
        }

        if (rating == null || rating < 1 || rating > 5) {
            throw new BusinessException("评分必须在1-5之间");
        }

        Evaluation evaluation = new Evaluation();
        evaluation.setOrderId(orderId);
        evaluation.setCustomerId(customerId);
        evaluation.setMerchantId(order.getMerchantId());
        evaluation.setRating(rating);
        evaluation.setContent(content);
        evaluationMapper.insert(evaluation);
    }

    public IPage<Evaluation> getMerchantEvaluations(int page, int size, Long merchantId) {
        Page<Evaluation> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Evaluation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Evaluation::getMerchantId, merchantId);
        wrapper.orderByDesc(Evaluation::getCreateTime);
        return evaluationMapper.selectPage(pageParam, wrapper);
    }

    public List<Evaluation> getMyEvaluations() {
        Long customerId = getCurrentUserId();
        return evaluationMapper.selectList(
                new LambdaQueryWrapper<Evaluation>().eq(Evaluation::getCustomerId, customerId)
                        .orderByDesc(Evaluation::getCreateTime)
        );
    }

    public Evaluation getOrderEvaluation(Long orderId) {
        return evaluationMapper.selectOne(
                new LambdaQueryWrapper<Evaluation>().eq(Evaluation::getOrderId, orderId)
        );
    }
}
