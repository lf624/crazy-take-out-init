package com.crazy.vo;

import com.crazy.entity.SetmealDish;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SetmealVO implements Serializable {
    private Long id;

    private Long categoryId;

    private String name;

    private BigDecimal price;

    private String image;

    private String description;

    private Integer status;

    private LocalDateTime updateTime;

    private String categoryName;

    private List<SetmealDish> setmealDishes = new ArrayList<>();
}
