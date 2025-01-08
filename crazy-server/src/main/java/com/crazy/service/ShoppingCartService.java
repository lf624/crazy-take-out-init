package com.crazy.service;

import com.crazy.dto.ShoppingCartDTO;
import com.crazy.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {

    void addShoppingCart(ShoppingCartDTO shoppingCartDTO);

    List<ShoppingCart> showShoppingCart();

    void cleanShoppingCart();

    void deleteOneItem(ShoppingCartDTO shoppingCartDTO);
}
