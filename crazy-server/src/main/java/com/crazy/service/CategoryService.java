package com.crazy.service;

import com.crazy.dto.CategoryDTO;
import com.crazy.dto.CategoryPageQueryDTO;
import com.crazy.entity.Category;
import com.crazy.result.PageResult;

import java.util.List;

public interface CategoryService {

    void save(CategoryDTO categoryDTO);

    void delete(Long id);

    void update(CategoryDTO categoryDTO);

    void startOrStop(Integer status, Long id);

    List<Category> getByType(Integer type);

    PageResult<Category> page(CategoryPageQueryDTO categoryPageQueryDTO);
}
