package com.crazy.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DishFlavor implements Serializable {
    private static final long serialVersionUID = 4L;

    private Long id;

    private Long dishId;

    private String name;

    private String value;
}