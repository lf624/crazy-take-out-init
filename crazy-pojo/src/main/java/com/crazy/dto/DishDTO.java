package com.crazy.dto;

import com.crazy.entity.DishFlavor;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Schema(description = "dish DTO")
public class DishDTO implements Serializable {
    private Long id;

    private String name;

    @Schema(description = "与category表关联的分类Id")
    private Long categoryId;

    private BigDecimal price;

    private String image;

    @Schema(description = "描述信息")
    private String description;

    private Integer status;

    @Schema(description = "菜品的多种不同风味")
    private List<DishFlavor> flavors = new ArrayList<>();
}
