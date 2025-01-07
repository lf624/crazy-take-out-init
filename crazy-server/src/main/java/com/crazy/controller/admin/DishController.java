package com.crazy.controller.admin;

import com.crazy.dto.DishDTO;
import com.crazy.dto.DishPageQueryDTO;
import com.crazy.entity.Dish;
import com.crazy.result.PageResult;
import com.crazy.result.Result;
import com.crazy.service.DishService;
import com.crazy.vo.DishVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/admin/dish")
@Tag(name = "菜品相关接口")
public class DishController {

    @Autowired
    DishService dishService;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @PostMapping
    @Operation(summary = "新增菜品")
    public Result<String> save(@RequestBody DishDTO dishDTO) {
        log.info("newly add dish: {}", dishDTO);
        dishService.saveWithFlavor(dishDTO);

        // 清理菜品缓存
        String key = "dish_" + dishDTO.getCategoryId();
        cleanCache(key);
        return Result.success();
    }

    @GetMapping("/page")
    @Operation(summary = "菜品分页查询")
    public Result<PageResult<DishVO>> page(DishPageQueryDTO dishPageQueryDTO) {
        log.info("dish page query: {}", dishPageQueryDTO);
        PageResult<DishVO> pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    @DeleteMapping
    @Operation(summary = "菜品批量删除")
    public Result<String> delete(@RequestParam List<Long> ids) {
        log.info("batch delete: {}", ids);
        dishService.deleteBatch(ids);

        // 将所有菜品缓存清理掉
        cleanCache("dish_*");
        return Result.success();
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据Id查询菜品")
    public Result<DishVO> getById(@PathVariable Long id) {
        log.info("get dish by id: {}", id);
        DishVO dishVO = dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }

    @PutMapping
    @Operation(summary = "修改菜品")
    public Result<String> updateDishWithFlavor(@RequestBody DishDTO dishDTO) {
        log.info("update dish: {}", dishDTO);
        dishService.updateDishWithFlavor(dishDTO);

        // 清理菜品缓存
        cleanCache("dish_" + dishDTO.getCategoryId());
        return Result.success();
    }

    @GetMapping("/list")
    @Operation(summary = "根据分类Id查询菜品")
    public Result<List<Dish>> getByCategoryId(@RequestParam Long categoryId) {
        log.info("get dish by category id: {}", categoryId);
        return Result.success(dishService.getByCategoryId(categoryId));
    }

    @PostMapping("/status/{status}")
    @Operation(summary = "启售停售菜品")
    public Result<String> startOrStop(@PathVariable Integer status, @RequestParam Long id) {
        log.info("change dish {} to status: {}", id, status);
        dishService.changeStatus(status, id);

        // 清理所有菜品缓存
        cleanCache("dish_*");
        return Result.success();
    }

    private void cleanCache(String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }
}
