package com.crazy.controller.user;

import com.crazy.constant.StatusConstant;
import com.crazy.entity.Setmeal;
import com.crazy.result.Result;
import com.crazy.service.SetmealService;
import com.crazy.vo.DishItemVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("userSetmealController")
@RequestMapping("/user/setmeal")
@Tag(name = "C端套餐浏览接口")
public class SetmealController {

    @Autowired
    SetmealService setmealService;

    @GetMapping("/list")
    @Operation(summary = "根据分类id查询套餐")
    public Result<List<Setmeal>> list(@RequestParam Long categoryId) {
        Setmeal setmeal = Setmeal.builder()
                .categoryId(categoryId)
                .status(StatusConstant.ENABLE)
                .build();
        List<Setmeal> setmeals = setmealService.list(setmeal);
        return Result.success(setmeals);
    }

    @GetMapping("/dish/{id}")
    @Operation(summary = "根据套餐id查询包含的菜品")
    public Result<List<DishItemVO>> listDishById(@PathVariable Long id) {
        List<DishItemVO> result = setmealService.listDishById(id);
        return Result.success(result);
    }
}
