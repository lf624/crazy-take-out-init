package com.crazy.controller.admin;

import com.crazy.dto.OrderCancelDTO;
import com.crazy.dto.OrderConfirmDTO;
import com.crazy.dto.OrderRejectionDTO;
import com.crazy.dto.OrderSearchDTO;
import com.crazy.result.PageResult;
import com.crazy.result.Result;
import com.crazy.service.OrderService;
import com.crazy.vo.OrderDetailVO;
import com.crazy.vo.OrderSearchVO;
import com.crazy.vo.OrderStatisticsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("adminOrderController")
@RequestMapping("/admin/order")
@Tag(name = "订单管理接口")
@Slf4j
public class OrderController {

    @Autowired
    OrderService orderService;

    @GetMapping("/conditionSearch")
    @Operation(summary = "订单搜索")
    public Result<PageResult<OrderSearchVO>> conditionSearch(OrderSearchDTO orderSearchDTO) {
        PageResult<OrderSearchVO> pageResult = orderService.conditionSearch(orderSearchDTO);
        return Result.success(pageResult);
    }

    @GetMapping("/statistics")
    @Operation(summary = "各个状态订单数量统计")
    public Result<OrderStatisticsVO> statistics() {
        OrderStatisticsVO orderStatisticsVO = orderService.statistics();
        return Result.success(orderStatisticsVO);
    }

    @GetMapping("/details/{id}")
    @Operation(summary = "查询订单详情")
    public Result<OrderDetailVO> getDetailById(@PathVariable Long id) {
        return Result.success(orderService.getById(id));
    }

    @PutMapping("/confirm")
    @Operation(summary = "接单")
    public Result<String> confirm(@RequestBody OrderConfirmDTO orderConfirmDTO) {
        log.info("confirm order: {}", orderConfirmDTO);
        orderService.confirm(orderConfirmDTO.getId());
        return Result.success();
    }

    @PutMapping("/rejection")
    @Operation(summary = "拒单")
    public Result<String> reject(@RequestBody OrderRejectionDTO orderRejectionDTO) {
        log.info("reject order: {}", orderRejectionDTO);
        orderService.reject(orderRejectionDTO);
        return Result.success();
    }

    @PutMapping("/cancel")
    @Operation(summary = "取消订单")
    public Result<String> cancel(@RequestBody OrderCancelDTO orderCancelDTO) {
        log.info("cancel order: {}", orderCancelDTO);
        orderService.cancel(orderCancelDTO);
        return Result.success();
    }

    @PutMapping("/delivery/{id}")
    @Operation(summary = "派送订单")
    public Result<String> delivery(@PathVariable Long id) {
        log.info("delivery order: {}", id);
        orderService.delivery(id);
        return Result.success();
    }

    @PutMapping("/complete/{id}")
    @Operation(summary = "完成订单")
    public Result<String> complete(@PathVariable Long id) {
        log.info("complete order: {}", id);
        orderService.complete(id);
        return Result.success();
    }
}
