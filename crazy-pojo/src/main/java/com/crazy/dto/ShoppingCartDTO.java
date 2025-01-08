package com.crazy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "Shopping cart dto")
public class ShoppingCartDTO implements Serializable {
    private Long dishId;

    private Long setmealId;

    private String dishFlavor;
}
