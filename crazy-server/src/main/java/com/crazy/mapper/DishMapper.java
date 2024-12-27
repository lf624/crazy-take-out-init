package com.crazy.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DishMapper {

    @Select("SELECT COUNT(id) FROM dish WHERE category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);
}
