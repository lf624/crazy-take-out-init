package com.crazy.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderPageQueryDTO implements Serializable {
    private Integer page;
    private Integer pageSize;
    private Integer status;
}
