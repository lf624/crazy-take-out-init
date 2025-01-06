package com.crazy.controller.user;

import com.crazy.result.Result;
import com.crazy.service.DishService;
import com.crazy.vo.DishVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("userDishController")
@RequestMapping("/user/dish")
@Tag(name = "C端菜品浏览端口")
public class DishController {

    @Autowired
    DishService dishService;

    @GetMapping("/list")
    @Operation(summary = "根据分类id查询菜品")
    public Result<List<DishVO>> list(@RequestParam Long categoryId) {
        List<DishVO> dishVOList = dishService.listWithFlavor(categoryId);
        return Result.success(dishVOList);
    }
}
