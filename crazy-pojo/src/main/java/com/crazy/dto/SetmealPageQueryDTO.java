package com.crazy.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "setmeal page query dto")
public class SetmealPageQueryDTO implements Serializable {
    @Schema(description = "页码")
    private int page;
    @Schema(description = "每页记录数")
    private int pageSize;
    @Schema(description = "分类Id")
    private Long categoryId;
    private String name;
    private Integer status;
}
