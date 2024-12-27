package com.crazy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "category DTO")
public class CategoryDTO {

    @Schema(description = "primary key")
    private Long id;

    @Schema(description = "分类名称")
    private String name;

    @Schema(description = "顺序")
    private Integer sort;

    @Schema(description = "类型，1 菜品分类 2 套餐分类")
    private Integer type;
}
