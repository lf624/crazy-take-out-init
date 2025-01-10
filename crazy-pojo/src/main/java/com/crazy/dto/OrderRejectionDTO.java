package com.crazy.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderRejectionDTO implements Serializable {
    private Long id;
    private String rejectionReason;
}
