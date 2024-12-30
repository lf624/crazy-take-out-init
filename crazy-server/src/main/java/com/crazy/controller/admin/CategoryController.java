package com.crazy.controller.admin;

import com.crazy.dto.CategoryDTO;
import com.crazy.dto.CategoryPageQueryDTO;
import com.crazy.entity.Category;
import com.crazy.exception.DeletionNotAllowedException;
import com.crazy.result.PageResult;
import com.crazy.result.Result;
import com.crazy.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/category")
@Slf4j
@Tag(name = "分类相关接口")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    @PostMapping
    @Operation(summary = "新增分类")
    public Result<String> save(@RequestBody CategoryDTO categoryDTO) {
        log.info("add new category:{}", categoryDTO);
        categoryService.save(categoryDTO);
        return Result.success();
    }

    @DeleteMapping
    @Operation(summary = "根据id删除分类")
    public Result<String> delete(@RequestParam Long id) {
        log.info("delete category id: {}", id);
        categoryService.delete(id);
        return Result.success();
    }

    @PutMapping
    @Operation(summary = "修改分类")
    public Result<String> update(@RequestBody CategoryDTO categoryDTO) {
        log.info("update category: {}", categoryDTO);
        categoryService.update(categoryDTO);
        return Result.success();
    }

    @PostMapping("/status/{status}")
    @Operation(summary = "启用或禁用分类")
    public Result<String> startOrStop(@PathVariable Integer status, @RequestParam Long id) {
        categoryService.startOrStop(status, id);
        return Result.success();
    }

    @GetMapping("/list")
    @Operation(summary = "根据类型查询分类")
    public Result<List<Category>> typeList(@RequestParam(required = false) Integer type) {
        List<Category> categories = categoryService.list(type);
        return Result.success(categories);
    }

    @GetMapping("/page")
    @Operation(summary = "分类分页查询")
    public Result<PageResult<Category>> page(CategoryPageQueryDTO categoryPageQueryDTO) {
        log.info("page query: {}", categoryPageQueryDTO);
        PageResult<Category> res = categoryService.page(categoryPageQueryDTO);
        return Result.success(res);
    }
}
