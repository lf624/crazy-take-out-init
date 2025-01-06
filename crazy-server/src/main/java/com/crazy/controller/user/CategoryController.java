package com.crazy.controller.user;

import com.crazy.entity.Category;
import com.crazy.result.Result;
import com.crazy.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("userCategoryController")
@RequestMapping("/user/category")
@Tag(name = "C端分类接口")
@Slf4j
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    @GetMapping("/list")
    @Operation(summary = "根据类型查询分类")
    public Result<List<Category>> list(@RequestParam(required = false) Integer type) {
        List<Category> categories = categoryService.list(type);
        return Result.success(categories);
    }
}
