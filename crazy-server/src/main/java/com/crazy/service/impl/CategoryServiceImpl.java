package com.crazy.service.impl;

import com.crazy.constant.MessageConstant;
import com.crazy.constant.StatusConstant;
import com.crazy.context.BaseContext;
import com.crazy.dto.CategoryDTO;
import com.crazy.dto.CategoryPageQueryDTO;
import com.crazy.entity.Category;
import com.crazy.exception.DeletionNotAllowedException;
import com.crazy.mapper.CategoryMapper;
import com.crazy.mapper.DishMapper;
import com.crazy.mapper.SetMealMapper;
import com.crazy.result.PageResult;
import com.crazy.service.CategoryService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    CategoryMapper categoryMapper;
    @Autowired
    DishMapper dishMapper;
    @Autowired
    SetMealMapper setMealMapper;

    @Override
    public void save(CategoryDTO categoryDTO) {
        Category category = new Category();

        BeanUtils.copyProperties(categoryDTO, category);
        category.setStatus(StatusConstant.DISABLE);
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        category.setCreateUser(BaseContext.getCurrentId());
        category.setUpdateUser(BaseContext.getCurrentId());

        categoryMapper.insert(category);
    }

    @Override
    public void delete(Long id) {
        Integer count = dishMapper.countByCategoryId(id);
        if(count > 0)
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);

        count = setMealMapper.countByCategoryId(id);
        if(count > 0)
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_SETMEAL);

        categoryMapper.delete(id);
    }

    @Override
    public void update(CategoryDTO categoryDTO) {
        Category category = new Category();

        BeanUtils.copyProperties(categoryDTO, category);
        category.setUpdateTime(LocalDateTime.now());
        category.setUpdateUser(BaseContext.getCurrentId());

        categoryMapper.update(category);
    }

    @Override
    public void startOrStop(Integer status, Long id) {
        Category category = new Category();

        category.setStatus(status);
        category.setId(id);
        category.setUpdateTime(LocalDateTime.now());
        category.setUpdateUser(BaseContext.getCurrentId());

        categoryMapper.update(category);
    }

    @Override
    public List<Category> list(Integer type) {
        return categoryMapper.list(type);
    }

    @Override
    public PageResult<Category> page(CategoryPageQueryDTO categoryPageQueryDTO) {
        int page = categoryPageQueryDTO.getPage();
        int pageSize = categoryPageQueryDTO.getPageSize();

        PageHelper.startPage(page, pageSize);
        Page<Category> query = categoryMapper.pageQuery(categoryPageQueryDTO);

        long total = query.getTotal();
        List<Category> record = query.getResult();
        return new PageResult<>(total, record);
    }
}
