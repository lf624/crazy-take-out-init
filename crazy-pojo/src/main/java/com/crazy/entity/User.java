package com.crazy.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
    private Long id;
    // 微信用户唯一标识
    private String openid;
    private String name;
    private String phone;
    private String sex;
    // 身份证号
    private String idNumber;
    // 头像
    private String avatar;
    private LocalDateTime createTime;
}
