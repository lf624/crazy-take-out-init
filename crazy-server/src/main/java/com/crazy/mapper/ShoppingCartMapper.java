package com.crazy.mapper;

import com.crazy.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    // 条件查询
    List<ShoppingCart> list(ShoppingCart shoppingCart);

    // 更新商品数量
    @Update("UPDATE shopping_cart SET number = #{number} WHERE id = #{id}")
    void updateNumberById(ShoppingCart shoppingCart);

    // 插入购物车数据
    @Insert("INSERT INTO shopping_cart (name, image, user_id, dish_id, setmeal_id, dish_flavor, amount, number, create_time) VALUES " +
            "(#{name}, #{image}, #{userId}, #{dishId}, #{setmealId}, #{dishFlavor}, #{amount}, #{number}, #{createTime})")
    void insert(ShoppingCart shoppingCart);

    @Delete("DELETE FROM shopping_cart WHERE user_id = #{userId}")
    void deleteByUserId(Long userId);

    void delete(ShoppingCart shoppingCart);
}
