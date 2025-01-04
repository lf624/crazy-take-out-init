package com.crazy.mapper;

import com.crazy.annotation.AutoFill;
import com.crazy.dto.DishPageQueryDTO;
import com.crazy.entity.Dish;
import com.crazy.enumeration.OperationType;
import com.crazy.vo.DishVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 查询特定分类Id下的菜品数量
     * @param categoryId
     * @return
     */
    @Select("SELECT COUNT(id) FROM dish WHERE category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    @AutoFill(OperationType.INSERT)
    void insert(Dish dish);

    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    @Select("SELECT * FROM dish WHERE id = #{id}")
    Dish getById(Long id);

    @Delete("DELETE FROM dish WHERE id = #{id}")
    void deleteById(Long id);

    @AutoFill(OperationType.UPDATE)
    void update(Dish dish);

    // 动态查询
    List<Dish> list(Dish dish);

    @Select("SELECT d.* FROM dish AS d LEFT JOIN setmeal_dish AS sd ON d.id = sd.dish_id WHERE sd.setmeal_id = #{setmealId}")
    List<Dish> getBySetmealId(Long setmealId);
}
