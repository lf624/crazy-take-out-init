package com.crazy.mapper;

import com.crazy.dto.SetmealPageQueryDTO;
import com.crazy.entity.Setmeal;
import com.crazy.entity.SetmealDish;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetMealDishMapper {

    List<Long> getSetMealIdsByDishIds(List<Long> dishIds);

    void insertBatch(List<SetmealDish> setmealDishes);

    @Select("SELECT * FROM setmeal_dish WHERE setmeal_id = #{setmealId}")
    List<SetmealDish> getBySetmealId(Long setmealId);

    @Delete("DELETE FROM setmeal_dish WHERE setmeal_id = #{setmealId}")
    void deleteBySetmealId(Long setmealId);

    List<Long> getDishIdsBySetmealIds(List<Long> setmealIds);
}
