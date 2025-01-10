package com.crazy.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderPaymentDTO implements Serializable {

    private String orderNumber;

    private Integer payMethod;
}
