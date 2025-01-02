package com.crazy.service.impl;

import com.crazy.constant.MessageConstant;
import com.crazy.constant.StatusConstant;
import com.crazy.dto.DishDTO;
import com.crazy.dto.DishPageQueryDTO;
import com.crazy.entity.Dish;
import com.crazy.entity.DishFlavor;
import com.crazy.exception.DeletionNotAllowedException;
import com.crazy.mapper.DishFlavorMapper;
import com.crazy.mapper.DishMapper;
import com.crazy.mapper.SetMealDishMapper;
import com.crazy.result.PageResult;
import com.crazy.service.DishService;
import com.crazy.vo.DishVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    DishMapper dishMapper;
    @Autowired
    DishFlavorMapper dishFlavorMapper;
    @Autowired
    SetMealDishMapper setMealDishMapper;

    /**
     * 新增菜品
     * @param dishDTO
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {
        // TODO 这里没有检查dish的分类ID是否存在
        Dish dish = new Dish();

        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.insert(dish);

        // 获取insert语句生成的主键值
        Long dishId = dish.getId();

        List<DishFlavor> dishFlavors = dishDTO.getFlavors();
        if(dishFlavors != null && !dishFlavors.isEmpty()) {
            dishFlavors.forEach(df -> df.setDishId(dishId));
            dishFlavorMapper.insertBatch(dishFlavors);
        }
    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult<>(page.getTotal(), page.getResult());
    }

    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        for(Long id : ids) {
            Dish dish = dishMapper.getById(id);
            // 当前菜品处于起售中，不能删除
            if(StatusConstant.ENABLE.equals(dish.getStatus()))
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
        }

        List<Long> setMealIds = setMealDishMapper.getSetMealIdsByDishIds(ids);
        if(setMealIds != null && !setMealIds.isEmpty())
            throw new DeletionNotAllowedException(MessageConstant.DiSH_BE_RELATED_BY_SETMEAL);

        for(Long id : ids) {
            // 删除菜品数据
            dishMapper.deleteById(id);
            // 删除菜品关联的口味数据
            dishFlavorMapper.deleteByDishId(id);
        }
        // TODO 是否需要删除云上存储的菜品图片
    }
}
