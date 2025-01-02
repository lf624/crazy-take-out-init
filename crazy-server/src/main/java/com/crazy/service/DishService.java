package com.crazy.service;

import com.crazy.dto.DishDTO;
import com.crazy.dto.DishPageQueryDTO;
import com.crazy.result.PageResult;
import com.crazy.vo.DishVO;

import java.util.List;

public interface DishService {

    void saveWithFlavor(DishDTO dishDTO);

    PageResult<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    void deleteBatch(List<Long> ids);
}
