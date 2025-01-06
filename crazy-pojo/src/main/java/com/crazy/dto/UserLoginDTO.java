package com.crazy.dto;

import lombok.Data;

import java.io.Serializable;

// C端用户登陆
@Data
public class UserLoginDTO implements Serializable {
    private String code;
}
