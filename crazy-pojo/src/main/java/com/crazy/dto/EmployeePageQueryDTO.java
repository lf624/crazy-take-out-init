package com.crazy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "employee page query DTO")
public class EmployeePageQueryDTO implements Serializable {
    @Schema(description = "员工姓名")
    private String name;

    @Schema(description = "页码")
    private int page;

    @Schema(description = "每页记录数")
    private int pageSize;
}
