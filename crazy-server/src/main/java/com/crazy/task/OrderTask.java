package com.crazy.task;

import com.crazy.constant.OrderStatus;
import com.crazy.entity.Orders;
import com.crazy.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class OrderTask {

    @Autowired
    OrderMapper orderMapper;

    // 处理支付超时订单
    @Scheduled(cron = "0 * * * * *")
    public void processTimeoutOrder() {
        LocalDateTime time = LocalDateTime.now().minusMinutes(15);

        // select * from orders where status = 1 and order_time < 当前时间-15分钟
        List<Orders> timeoutOrders = orderMapper.getByStatusAndOrderTimeLT(OrderStatus.PENDING_PAYMENT, time);
        if(timeoutOrders != null && !timeoutOrders.isEmpty()) {
            timeoutOrders.forEach(orders -> {
                orders.setStatus(OrderStatus.CANCELED);
                orders.setCancelReason("订单支付超时，自动取消");
                orders.setCancelTime(LocalDateTime.now());
                orderMapper.update(orders);
            });
        }
    }

    // 处理“派送中”状态的订单
    @Scheduled(cron = "0 0 1 * * *")
    public void processDeliveryOrder() {
        log.info("process delivery order: {}", new Date());
        // select * from orders where status = 4 and order_time < 当前时间-1小时
        LocalDateTime time = LocalDateTime.now().minusHours(1);
        List<Orders> deliveries = orderMapper.getByStatusAndOrderTimeLT(OrderStatus.DELIVERY_IN_PROGRESS, time);

        if(deliveries != null && !deliveries.isEmpty()) {
            deliveries.forEach(order -> {
                order.setStatus(OrderStatus.COMPLETED);
                orderMapper.update(order);
            });
        }
    }
}
