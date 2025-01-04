package com.crazy.service.impl;

import com.crazy.constant.MessageConstant;
import com.crazy.constant.StatusConstant;
import com.crazy.dto.SetmealDTO;
import com.crazy.dto.SetmealPageQueryDTO;
import com.crazy.entity.Dish;
import com.crazy.entity.Setmeal;
import com.crazy.entity.SetmealDish;
import com.crazy.exception.DeletionNotAllowedException;
import com.crazy.mapper.DishMapper;
import com.crazy.mapper.SetMealDishMapper;
import com.crazy.mapper.SetMealMapper;
import com.crazy.result.PageResult;
import com.crazy.service.SetmealService;
import com.crazy.vo.SetmealVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    SetMealMapper setMealMapper;
    @Autowired
    SetMealDishMapper setMealDishMapper;
    @Autowired
    DishMapper dishMapper;

    @Override
    @Transactional
    public void save(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setMealMapper.insert(setmeal);

        Long setmealId = setmeal.getId();

        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if(setmealDishes != null && !setmealDishes.isEmpty()) {
            setmealDishes.forEach(sd -> sd.setSetmealId(setmealId));
            setMealDishMapper.insertBatch(setmealDishes);
        }
    }

    @Override
    public SetmealVO getByIdWithDish(Long id) {
        SetmealVO setmealVO = setMealMapper.getByIdWithCategoryName(id);

        List<SetmealDish> setmealDishes = setMealDishMapper.getBySetmealId(setmealVO.getId());
        if(setmealDishes != null && !setmealDishes.isEmpty())
            setmealVO.setSetmealDishes(setmealDishes);

        return setmealVO;
    }

    @Override
    public PageResult<SetmealVO> page(SetmealPageQueryDTO dto) {
        PageHelper.startPage(dto.getPage(), dto.getPageSize());
        Page<SetmealVO> page = setMealMapper.pageQuery(dto);
        return new PageResult<>(page.getTotal(), page.getResult());
    }

    @Override
    @Transactional
    public void updateWithDish(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);

        setMealMapper.update(setmeal);

        // 删除原有的关联菜品
        setMealDishMapper.deleteBySetmealId(setmealDTO.getId());

        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if(setmealDishes != null && !setmealDishes.isEmpty()) {
            setmealDishes.forEach(sd -> sd.setSetmealId(setmealDTO.getId()));
            setMealDishMapper.insertBatch(setmealDishes);
        }
    }

    @Override
    @Transactional
    public void changeStatus(Integer status, Long id) {
        Setmeal setmeal = Setmeal.builder()
                .id(id)
                .status(status)
                .build();
        setMealMapper.update(setmeal);

        if(StatusConstant.ENABLE.equals(status)) {
            List<Long> dishIds = setMealDishMapper.getDishIdsBySetmealIds(List.of(id));
            for(Long dishId : dishIds) {
                Dish dish = Dish.builder()
                        .id(dishId)
                        .status(StatusConstant.ENABLE)
                        .build();
                dishMapper.update(dish);
            }
        }
    }

    @Override
    public void deleteBatch(List<Long> ids) {
        for(Long id : ids) {
            Setmeal setmeal = setMealMapper.getById(id);
            if(StatusConstant.ENABLE.equals(setmeal.getStatus()))
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            setMealMapper.deleteById(id);
            setMealDishMapper.deleteBySetmealId(id);
        }
    }
}
