package com.crazy.mapper;

import com.crazy.annotation.AutoFill;
import com.crazy.dto.SetmealPageQueryDTO;
import com.crazy.entity.Setmeal;
import com.crazy.enumeration.OperationType;
import com.crazy.vo.DishItemVO;
import com.crazy.vo.SetmealVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface SetMealMapper {

    @Select("SELECT COUNT(id) FROM setmeal WHERE category_id = #{categoryId}")
    Integer countByCategoryId(Long id);

    @AutoFill(OperationType.UPDATE)
    void update(Setmeal setmeal);

    @AutoFill(OperationType.INSERT)
    void insert(Setmeal setmeal);

    @Select("SELECT s.*, c.name AS categoryName FROM setmeal AS s LEFT JOIN category AS c ON s.category_id = c.id WHERE s.id = #{id}")
    SetmealVO getByIdWithCategoryName(Long id);

    Page<SetmealVO> pageQuery(SetmealPageQueryDTO dto);

    @Select("SELECT * FROM setmeal WHERE id = #{id}")
    Setmeal getById(Long id);

    @Delete("DELETE FROM setmeal WHERE id = #{id}")
    void deleteById(Long id);

    List<Setmeal> list(Setmeal setmeal);

    @Select("SELECT d.name, d.image,d.description, sd.copies FROM setmeal_dish sd " +
            "LEFT JOIN dish d ON sd.dish_id = d.id WHERE sd.setmeal_id = #{setmealId}")
    List<DishItemVO> getDishItemBySetmealId(Long setmealId);

    Integer countByMap(Map<String, Object> map);
}
