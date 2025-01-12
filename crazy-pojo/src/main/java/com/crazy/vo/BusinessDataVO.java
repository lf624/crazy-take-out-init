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
public class BusinessDataVO implements Serializable {
    // 新增用户数
    private Integer newUsers;

    // 订单完成率
    private Double orderCompletionRate;

    // 营业额
    private Double turnover;

    // 平均客单价
    private Double unitPrice;

    // 有效订单数
    private Integer validOrderCount;
}
