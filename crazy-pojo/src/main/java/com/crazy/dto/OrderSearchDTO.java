package com.crazy.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class OrderSearchDTO implements Serializable {
    private Integer page;
    private Integer pageSize;
    private Integer status;
    private String number;
    private String phone;
    private String beginTime;
    private String endTime;
}
