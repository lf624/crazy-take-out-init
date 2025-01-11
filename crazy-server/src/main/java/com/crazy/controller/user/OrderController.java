package com.crazy.controller.user;

import com.crazy.dto.OrderPageQueryDTO;
import com.crazy.dto.OrderPaymentDTO;
import com.crazy.dto.OrderSubmitDTO;
import com.crazy.result.PageResult;
import com.crazy.result.Result;
import com.crazy.service.OrderService;
import com.crazy.vo.OrderDetailVO;
import com.crazy.vo.OrderPaymentVO;
import com.crazy.vo.OrderSubmitVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController("userOrderController")
@RequestMapping("/user/order")
@Tag(name = "订单接口")
public class OrderController {

    @Autowired
    OrderService orderService;

    @PostMapping("/submit")
    @Operation(summary = "用户下单")
    public Result<OrderSubmitVO> submit(@RequestBody OrderSubmitDTO orderSubmitDTO) {
        log.info("order submit: {}", orderSubmitDTO);
        OrderSubmitVO orderSubmitVO = orderService.submitOrder(orderSubmitDTO);
        return Result.success(orderSubmitVO);
    }

    @PutMapping("/payment")
    @Operation(summary = "订单支付")
    public Result<OrderPaymentVO> payment(@RequestBody OrderPaymentDTO orderPaymentDTO) {
        log.info("order payment: {}", orderPaymentDTO);
        OrderPaymentVO orderPaymentVO = orderService.payment(orderPaymentDTO);
        return Result.success(orderPaymentVO);
    }

    @GetMapping("/orderDetail/{id}")
    @Operation(summary = "查询订单详情")
    public Result<OrderDetailVO> getById(@PathVariable Long id) {
        OrderDetailVO orderDetailVO = orderService.getById(id);
        return Result.success(orderDetailVO);
    }

    @GetMapping("/historyOrders")
    @Operation(summary = "历史订单查询")
    public Result<PageResult<OrderDetailVO>> pageQuery(OrderPageQueryDTO orderPageQueryDTO) {
        PageResult<OrderDetailVO> pageResult = orderService.pageQuery(orderPageQueryDTO);
        return Result.success(pageResult);
    }

    @PutMapping("/cancel/{id}")
    @Operation(summary = "取消订单")
    public Result<String> cancel(@PathVariable Long id) {
        log.info("cancel order: {}", id);
        orderService.userCancelById(id);
        return Result.success();
    }

    @PostMapping("/repetition/{id}")
    @Operation(summary = "再来一单")
    public Result<String> repeat(@PathVariable Long id) {
        orderService.repeat(id);
        return Result.success();
    }
}
