package com.crazy.service.impl;

import com.crazy.constant.OrderStatus;
import com.crazy.constant.StatusConstant;
import com.crazy.mapper.DishMapper;
import com.crazy.mapper.OrderMapper;
import com.crazy.mapper.SetMealMapper;
import com.crazy.mapper.UserMapper;
import com.crazy.service.ReportService;
import com.crazy.service.WorkspaceService;
import com.crazy.vo.BusinessDataVO;
import com.crazy.vo.DishOverviewVO;
import com.crazy.vo.OrderOverviewVO;
import com.crazy.vo.SetmealOverviewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class WorkspaceServiceImpl implements WorkspaceService {

    @Autowired
    UserMapper userMapper;
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    SetMealMapper setMealMapper;
    @Autowired
    DishMapper dishMapper;

    @Override
    public BusinessDataVO getBusinessData(LocalDateTime begin, LocalDateTime end) {
        Map<String, Object> map = new HashMap<>();
        map.put("begin", begin);
        map.put("end", end);

        Integer newUsers = userMapper.countByMap(map);
        Integer totalOrderCount = orderMapper.countByMap(map);

        map.put("status", OrderStatus.COMPLETED);
        Integer validOrderCount = orderMapper.countByMap(map);
        Double turnover = orderMapper.sumByMap(map);
        turnover = turnover == null ? 0.0 : turnover;

        double orderCompletionRate = 0.0;
        double unitPrice = 0.0;
        if(totalOrderCount > 0 && validOrderCount > 0) {
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;
            unitPrice = turnover / validOrderCount;
        }
        return BusinessDataVO.builder()
                .newUsers(newUsers)
                .turnover(turnover)
                .validOrderCount(validOrderCount)
                .unitPrice(unitPrice)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    @Override
    public SetmealOverviewVO getSetmealOverview() {
        Map<String, Object> map = new HashMap<>();
        map.put("status", StatusConstant.ENABLE);
        Integer enables = setMealMapper.countByMap(map);

        map.put("status", StatusConstant.DISABLE);
        Integer disables = setMealMapper.countByMap(map);
        return SetmealOverviewVO.builder()
                .sold(enables)
                .discontinued(disables)
                .build();
    }

    @Override
    public DishOverviewVO getDishOverview() {
        Map<String, Object> map = new HashMap<>();
        map.put("status", StatusConstant.ENABLE);
        Integer enables = dishMapper.countByMap(map);

        map.put("status", StatusConstant.DISABLE);
        Integer disables = dishMapper.countByMap(map);
        return DishOverviewVO.builder()
                .sold(enables)
                .discontinued(disables)
                .build();
    }

    @Override
    public OrderOverviewVO getOrderOverview() {
        Map<String, Object> map = new HashMap<>();
        map.put("begin", LocalDateTime.now().with(LocalTime.MIN));

        map.put("status", OrderStatus.TO_BE_CONFIRMED);
        Integer waitingCount = orderMapper.countByMap(map);

        map.put("status", OrderStatus.CONFIRMED);
        Integer delivered = orderMapper.countByMap(map);

        map.put("status", OrderStatus.CANCELED);
        Integer cancelled = orderMapper.countByMap(map);

        map.put("status", OrderStatus.COMPLETED);
        Integer completed = orderMapper.countByMap(map);

        map.put("status", null);
        Integer total = orderMapper.countByMap(map);

        return OrderOverviewVO.builder()
                .waitingOrders(waitingCount)
                .deliveredOrders(delivered)
                .cancelledOrders(cancelled)
                .completedOrders(completed)
                .allOrders(total)
                .build();
    }
}
