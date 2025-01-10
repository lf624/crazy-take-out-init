package com.crazy.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Orders implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    // 订单号
    private String number;

    // 订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消
    private Integer status;

    private Long userId;

    private Long addressBookId;

    // 下单时间
    private LocalDateTime orderTime;

    // 结账时间
    private LocalDateTime checkoutTime;

    // 支付方式 1微信,2支付宝
    private Integer payMethod;

    // 支付状态 0未支付 1已支付 2退款
    private Integer payStatus;

    // 实收金额
    private BigDecimal amount;

    // 备注
    private String remark;

    private String phone;

    private String address;

    private String userName;

    // 收货人
    private String consignee;

    // 订单取消原因
    private String cancelReason;

    // 订单拒绝原因
    private String rejectionReason;

    private LocalDateTime cancelTime;

    private LocalDateTime estimatedDeliveryTime;

    // 送达状态
    private Integer deliveryStatus;

    private LocalDateTime deliveryTime;

    // 打包费
    private Integer packAmount;

    // 餐具数量
    private Integer tablewareNumber;

    // 餐具数量状态  1按餐量提供  0选择具体数量
    private Integer tablewareStatus;
}
