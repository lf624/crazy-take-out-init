package com.crazy.service;

import com.crazy.dto.*;
import com.crazy.result.PageResult;
import com.crazy.vo.*;

public interface OrderService {
    OrderSubmitVO submitOrder(OrderSubmitDTO orderSubmitDTO);

    OrderPaymentVO payment(OrderPaymentDTO orderPaymentDTO);

    // 支付成功，修改订单状态
    void paySuccess(String outTradeNo);

    OrderDetailVO getById(Long id);

    PageResult<OrderDetailVO> pageQuery(OrderPageQueryDTO orderPageQueryDTO);

    void userCancelById(Long id);

    void repeat(Long id);

    PageResult<OrderSearchVO> conditionSearch(OrderSearchDTO orderSearchDTO);

    OrderStatisticsVO statistics();

    void confirm(Long id);

    void reject(OrderRejectionDTO orderRejectionDTO);

    void cancel(OrderCancelDTO orderCancelDTO);

    void delivery(Long id);

    void complete(Long id);
}
