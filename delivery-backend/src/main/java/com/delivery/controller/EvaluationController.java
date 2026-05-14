package com.delivery.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.delivery.common.Result;
import com.delivery.entity.Evaluation;
import com.delivery.service.EvaluationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/evaluation")
@RequiredArgsConstructor
@Tag(name = "评价管理", description = "订单评价相关接口")
public class EvaluationController {

    private final EvaluationService evaluationService;

    @Operation(summary = "提交评价", description = "对已完成订单进行评分和文字评价")
    @PostMapping
    public Result<Void> createEvaluation(
            @Parameter(description = "订单ID") @RequestParam Long orderId,
            @Parameter(description = "评分1-5") @RequestParam Integer rating,
            @Parameter(description = "评价内容") @RequestParam(required = false) String content) {
        evaluationService.createEvaluation(orderId, rating, content);
        return Result.success(null);
    }

    @Operation(summary = "获取商家评价列表", description = "分页查询指定商家的所有评价")
    @GetMapping("/merchant/{merchantId}")
    public Result<IPage<Evaluation>> getMerchantEvaluations(
            @Parameter(description = "商家ID") @PathVariable Long merchantId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int size) {
        return Result.success(evaluationService.getMerchantEvaluations(page, size, merchantId));
    }

    @Operation(summary = "获取我的评价列表", description = "获取当前用户的所有评价记录")
    @GetMapping("/my")
    public Result<List<Evaluation>> getMyEvaluations() {
        return Result.success(evaluationService.getMyEvaluations());
    }

    @Operation(summary = "获取订单评价", description = "查看指定订单的评价详情")
    @GetMapping("/order/{orderId}")
    public Result<Evaluation> getOrderEvaluation(
            @Parameter(description = "订单ID") @PathVariable Long orderId) {
        return Result.success(evaluationService.getOrderEvaluation(orderId));
    }
}
