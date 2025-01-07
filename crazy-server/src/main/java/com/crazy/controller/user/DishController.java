package com.crazy.controller.user;

import com.crazy.result.Result;
import com.crazy.service.DishService;
import com.crazy.vo.DishVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/list")
    @Operation(summary = "根据分类id查询菜品")
    @SuppressWarnings("unchecked")
    public Result<List<DishVO>> list(@RequestParam Long categoryId) {
        // 先查看缓存
        String key = "dish_" + categoryId;
        List<DishVO> list = (List<DishVO>) redisTemplate.opsForValue().get(key);

        // 如果缓存中有，直接返回
        if(list != null && !list.isEmpty())
            return Result.success(list);
        // 若不存在，查询数据库，并将结果放入缓存中
        list = dishService.listWithFlavor(categoryId);
        redisTemplate.opsForValue().set(key, list);

        return Result.success(list);
    }
}
