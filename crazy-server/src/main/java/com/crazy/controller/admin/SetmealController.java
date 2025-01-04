package com.crazy.controller.admin;

import com.crazy.dto.SetmealDTO;
import com.crazy.dto.SetmealPageQueryDTO;
import com.crazy.entity.Setmeal;
import com.crazy.result.PageResult;
import com.crazy.result.Result;
import com.crazy.service.SetmealService;
import com.crazy.vo.SetmealVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/setmeal")
@Tag(name = "套餐相关接口")
public class SetmealController {

    @Autowired
    SetmealService setmealService;

    @PostMapping
    @Operation(summary = "新增套餐")
    public Result<String> save(@RequestBody SetmealDTO setmealDTO) {
        log.info("newly add setmeal: {}", setmealDTO);
        setmealService.save(setmealDTO);
        return Result.success();
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据id查询套餐")
    public Result<SetmealVO> getById(@PathVariable Long id) {
        log.info("get setmeal by id: {}", id);
        SetmealVO setmealVO = setmealService.getByIdWithDish(id);
        return Result.success(setmealVO);
    }

    @GetMapping("/page")
    @Operation(summary = "套餐分页查询")
    public Result<PageResult<SetmealVO>> page(SetmealPageQueryDTO dto) {
        log.info("setmeal page query: {}", dto);
        PageResult<SetmealVO> pageResult = setmealService.page(dto);
        return Result.success(pageResult);
    }

    @PutMapping
    @Operation(summary = "修改套餐")
    public Result<String> updateWithDish(@RequestBody SetmealDTO setmealDTO) {
        log.info("update setmeal: {}", setmealDTO);
        setmealService.updateWithDish(setmealDTO);
        return Result.success();
    }

    @PostMapping("/status/{status}")
    @Operation(summary = "套餐起售停售")
    public Result<String> StartOrStop(@PathVariable Integer status, @RequestParam Long id) {
        setmealService.changeStatus(status, id);
        return Result.success();
    }

    @DeleteMapping
    @Operation(summary = "批量删除套餐")
    public Result<String> delete(@RequestParam List<Long> ids) {
        setmealService.deleteBatch(ids);
        return Result.success();
    }
}
