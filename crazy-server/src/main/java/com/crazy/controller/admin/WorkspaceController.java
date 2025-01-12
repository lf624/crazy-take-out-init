package com.crazy.controller.admin;

import com.crazy.result.Result;
import com.crazy.service.WorkspaceService;
import com.crazy.vo.BusinessDataVO;
import com.crazy.vo.DishOverviewVO;
import com.crazy.vo.OrderOverviewVO;
import com.crazy.vo.SetmealOverviewVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@RestController
@RequestMapping("/admin/workspace")
@Tag(name = "工作台接口")
public class WorkspaceController {

    @Autowired
    private WorkspaceService workspaceService;

    @GetMapping("/businessData")
    @Operation(summary = "查询今日运营数据")
    public Result<BusinessDataVO> businessData() {
        LocalDate date = LocalDate.now();
        LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

        BusinessDataVO businessData = workspaceService.getBusinessData(beginTime, endTime);
        return Result.success(businessData);
    }

    @GetMapping("/overviewSetmeals")
    @Operation(summary = "查询套餐总览")
    public Result<SetmealOverviewVO> setmealOverview() {
        return Result.success(workspaceService.getSetmealOverview());
    }

    @GetMapping("/overviewDishes")
    @Operation(summary = "查询菜品总览")
    public Result<DishOverviewVO> dishOverview() {
        return Result.success(workspaceService.getDishOverview());
    }

    @GetMapping("/overviewOrders")
    @Operation(summary = "查询订单管理数据")
    public Result<OrderOverviewVO> orderOverview() {
        return Result.success(workspaceService.getOrderOverview());
    }
}
