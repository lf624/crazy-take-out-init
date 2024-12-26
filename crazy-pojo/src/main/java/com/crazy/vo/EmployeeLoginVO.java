package com.crazy.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "employee login view object")
public class EmployeeLoginVO implements Serializable {

    @Schema(description = "primary key")
    private Long id;
    @Schema(description = "username")
    private String username;
    @Schema(description = "name")
    private String name;
    @Schema(description = "json web token")
    private String token;

}
