package com.crazy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "employee login data transfer object")
public class EmployeeLoginDTO implements Serializable {

    @Schema(description = "username")
    private String username;
    @Schema(description = "password")
    private String password;

}
