package com.crazy.dto;

import com.crazy.entity.SetmealDish;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Schema(description = "setmeal dto")
public class SetmealDTO implements Serializable {
    private Long id;

    private String name;

    private Long categoryId;

    private BigDecimal price;

    private String image;

    private String description;

    private Integer status;

    @Schema(description = "套餐包含的菜品")
    private List<SetmealDish> setmealDishes = new ArrayList<>();
}
