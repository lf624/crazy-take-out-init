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
public class SetmealOverviewVO implements Serializable {
    // 已停售套餐数量
    private Integer discontinued;
    // 已启售套餐数量
    private Integer sold;
}
