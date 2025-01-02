package com.crazy.service;

import com.crazy.dto.DishDTO;
import com.crazy.dto.DishPageQueryDTO;
import com.crazy.result.PageResult;
import com.crazy.vo.DishVO;

public interface DishService {

    void saveWithFlavor(DishDTO dishDTO);

    PageResult<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);
}
