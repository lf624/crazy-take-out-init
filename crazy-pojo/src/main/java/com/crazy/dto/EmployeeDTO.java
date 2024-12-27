package com.crazy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "employee newly add DTO")
public class EmployeeDTO implements Serializable {
    @Schema(description = "primary key")
    private Long id;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "用户名")
    private String username;

    private String phone;

    private String sex;

    @Schema(description = "手机号")
    private String idNumber;
}
