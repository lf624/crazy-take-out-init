package com.crazy.service.impl;

import com.crazy.dto.DishDTO;
import com.crazy.entity.Dish;
import com.crazy.entity.DishFlavor;
import com.crazy.mapper.DishFlavorMapper;
import com.crazy.mapper.DishMapper;
import com.crazy.service.DishService;
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

    @Override
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {
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
}
