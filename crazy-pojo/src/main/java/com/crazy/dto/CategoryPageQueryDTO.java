package com.crazy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.Serializable;

@Data
@Schema(description = "category page query DTO")
public class CategoryPageQueryDTO implements Serializable {

    @Schema(description = "分类名称")
    private String name;

    @Schema(description = "页码")
    private int page;

    @Schema(description = "每页记录数")
    private int pageSize;

    @Schema(description = "分类类型：1为菜品分类，2为套餐分类")
    private Integer type;
}
