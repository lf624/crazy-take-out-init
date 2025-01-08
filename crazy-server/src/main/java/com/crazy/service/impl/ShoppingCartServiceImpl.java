package com.crazy.service.impl;

import com.crazy.context.BaseContext;
import com.crazy.dto.ShoppingCartDTO;
import com.crazy.entity.Dish;
import com.crazy.entity.Setmeal;
import com.crazy.entity.ShoppingCart;
import com.crazy.mapper.DishMapper;
import com.crazy.mapper.SetMealMapper;
import com.crazy.mapper.ShoppingCartMapper;
import com.crazy.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    ShoppingCartMapper shoppingCartMapper;
    @Autowired
    DishMapper dishMapper;
    @Autowired
    SetMealMapper setMealMapper;

    @Override
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        // 只能查询自己的购物车数据
        shoppingCart.setUserId(BaseContext.getCurrentId());

        // 判断当前商品是否在购物车中
        List<ShoppingCart> scList = shoppingCartMapper.list(shoppingCart);
        if(scList != null && scList.size() == 1) {
            //如果已经存在，就更新数量，数量加1
            ShoppingCart sc = scList.get(0);
            sc.setNumber(sc.getNumber() + 1);
            shoppingCartMapper.updateNumberById(sc);
        } else {
            //如果不存在，插入数据，数量就是1
            //判断当前添加到购物车的是菜品还是套餐
            Long dishId = shoppingCart.getDishId();
            if(dishId != null) {
                Dish dish = dishMapper.getById(dishId);
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());
            } else {
                Setmeal setmeal = setMealMapper.getById(shoppingCart.getSetmealId());
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());
            }
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(shoppingCart);
        }
    }

    @Override
    public List<ShoppingCart> showShoppingCart() {
        ShoppingCart shoppingCart = ShoppingCart.builder()
                .userId(BaseContext.getCurrentId())
                .build();
        return shoppingCartMapper.list(shoppingCart);
    }

    @Override
    public void cleanShoppingCart() {
        shoppingCartMapper.deleteByUserId(BaseContext.getCurrentId());
    }

    @Override
    public void deleteOneItem(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);

        shoppingCart = shoppingCartMapper.list(shoppingCart).get(0);
        Integer number = shoppingCart.getNumber();
        if(number > 1) {
            shoppingCart.setNumber(number - 1);
            shoppingCartMapper.updateNumberById(shoppingCart);
        } else {
            shoppingCartMapper.delete(shoppingCart);
        }
    }
}
