package com.crazy.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SetMealMapper {

    @Select("SELECT COUNT(id) FROM setmeal WHERE category_id = #{categoryId}")
    Integer countByCategoryId(Long id);
}
