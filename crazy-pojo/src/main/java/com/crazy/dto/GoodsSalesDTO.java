package com.crazy.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class GoodsSalesDTO implements Serializable {
    private String name; // 商品名称

    private Integer number; // 销量
}
