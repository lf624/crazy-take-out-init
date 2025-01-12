package com.crazy.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DishOverviewVO implements Serializable {
    // 已停售菜品数量
    private Integer discontinued;
    // 已启售菜品数量
    private Integer sold;
}
