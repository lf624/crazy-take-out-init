package com.crazy.service;

import com.crazy.dto.SetmealDTO;
import com.crazy.dto.SetmealPageQueryDTO;
import com.crazy.entity.Setmeal;
import com.crazy.result.PageResult;
import com.crazy.vo.SetmealVO;

import java.util.List;

public interface SetmealService {

    void save(SetmealDTO setmealDTO);

    SetmealVO getByIdWithDish(Long id);

    PageResult<SetmealVO> page(SetmealPageQueryDTO dto);

    void updateWithDish(SetmealDTO setmealDTO);

    void changeStatus(Integer status, Long id);

    void deleteBatch(List<Long> ids);
}
