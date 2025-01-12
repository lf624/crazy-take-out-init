package com.crazy.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderOverviewVO implements Serializable {
    private Integer allOrders;

    private Integer cancelledOrders; // 已取消数量

    private Integer completedOrders; // 已完成数量

    private Integer deliveredOrders; // 待派送数量

    private Integer waitingOrders; // 待接单数量
}
