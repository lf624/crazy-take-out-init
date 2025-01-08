package com.crazy.controller.user;

import com.crazy.dto.ShoppingCartDTO;
import com.crazy.entity.ShoppingCart;
import com.crazy.result.Result;
import com.crazy.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/shoppingCart")
@Tag(name = "C端购物车接口")
public class ShoppingCartController {

    @Autowired
    ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    @Operation(summary = "添加购物车")
    public Result<String> add(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        shoppingCartService.addShoppingCart(shoppingCartDTO);
        return Result.success();
    }

    @GetMapping("/list")
    @Operation(summary = "查看购物车")
    public Result<List<ShoppingCart>> list() {
        return Result.success(shoppingCartService.showShoppingCart());
    }

    @DeleteMapping("/clean")
    @Operation(summary = "清空购物车")
    public Result<String> clean() {
        shoppingCartService.cleanShoppingCart();
        return Result.success();
    }

    @PostMapping("/sub")
    @Operation(summary = "删除购物车中一个商品")
    public Result<String> sub(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        shoppingCartService.deleteOneItem(shoppingCartDTO);
        return Result.success();
    }
}
