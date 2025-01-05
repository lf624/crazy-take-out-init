package com.crazy.controller.admin;

import com.crazy.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Tag(name = "店铺操作接口")
public class ShopController {
    public static final String KEY = "SHOP_STATUS";

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @PutMapping("/{status}")
    @Operation(summary = "设置营业状态")
    public Result<String> setStatus(@PathVariable Integer status) {
        log.info("set shop status to: {}", status == 1 ? "open" : "close");
        redisTemplate.opsForValue().set(KEY, status);
        return Result.success();
    }

    @GetMapping("/status")
    @Operation(summary = "获取营业状态")
    public Result<Integer> getStatus() {
        Integer status = (Integer) redisTemplate.opsForValue().get(KEY);
        log.info("get shop status: {}", Integer.valueOf(1).equals(status) ? "open" : "close");
        return Result.success(status);
    }
}
