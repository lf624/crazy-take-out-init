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
public class OrderStatisticsVO implements Serializable {
    // 待派送数量
    private int confirmed;
    // 派送中数量
    private int deliveryInProgress;
    // 待接单数量
    private int toBeConfirmed;
}
