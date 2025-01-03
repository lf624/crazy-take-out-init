package com.crazy.service.impl;

import com.crazy.constant.MessageConstant;
import com.crazy.constant.StatusConstant;
import com.crazy.dto.DishDTO;
import com.crazy.dto.DishPageQueryDTO;
import com.crazy.entity.Dish;
import com.crazy.entity.DishFlavor;
import com.crazy.entity.Setmeal;
import com.crazy.exception.DeletionNotAllowedException;
import com.crazy.mapper.DishFlavorMapper;
import com.crazy.mapper.DishMapper;
import com.crazy.mapper.SetMealDishMapper;
import com.crazy.mapper.SetMealMapper;
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
    @Autowired
    SetMealMapper setMealMapper;

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

    /**
     * 根据id查询菜品和对应的口味数据
     * @param id
     * @return
     */
    @Override
    public DishVO getByIdWithFlavor(Long id) {
        DishVO dishVO = new DishVO();
        Dish dish = dishMapper.getById(id); // TODO dish可能为null
        BeanUtils.copyProperties(dish, dishVO);

        List<DishFlavor> dishFlavors = dishFlavorMapper.getByDishId(id);
        dishVO.setFlavors(dishFlavors);

        return dishVO;
    }

    /**
     * 根据id修改菜品基本信息和对应的口味信息
     * @param dishDTO
     */
    @Override
    public void updateDishWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        //修改菜品表基本信息
        dishMapper.update(dish);

        //删除原有的口味数据
        dishFlavorMapper.deleteByDishId(dishDTO.getId());

        //重新插入口味数据
        List<DishFlavor> dishFlavors = dishDTO.getFlavors();
        if(dishFlavors != null && !dishFlavors.isEmpty()) {
            dishFlavors.forEach(df -> df.setDishId(dishDTO.getId()));
            dishFlavorMapper.insertBatch(dishFlavors);
        }
    }

    @Override
    public List<Dish> getByCategoryId(Long categoryId) {
        return dishMapper.getByCategoryId(categoryId);
    }

    @Override
    public void changeStatus(Integer status, Long id) {
        Dish dish = Dish.builder()
                .id(id)
                .status(status)
                .build();
        dishMapper.update(dish);

        // 若菜品禁售，与之关联的套餐也要禁售
        if(StatusConstant.DISABLE.equals(status)) {
            List<Long> setmealIds = setMealDishMapper.getSetMealIdsByDishIds(List.of(id));
            if(setmealIds != null && !setmealIds.isEmpty()) {
                for(Long setmealId : setmealIds) {
                    Setmeal setmeal = Setmeal.builder()
                            .id(setmealId)
                            .status(status)
                            .build();
                    setMealMapper.update(setmeal);
                }
            }
        }
    }
}
