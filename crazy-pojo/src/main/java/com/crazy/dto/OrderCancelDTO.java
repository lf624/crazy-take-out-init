package com.crazy.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderCancelDTO implements Serializable {
    private Long id;
    private String cancelReason;
}
