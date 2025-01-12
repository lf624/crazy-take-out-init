package com.crazy.service;

import com.crazy.vo.BusinessDataVO;
import com.crazy.vo.DishOverviewVO;
import com.crazy.vo.OrderOverviewVO;
import com.crazy.vo.SetmealOverviewVO;

import java.time.LocalDateTime;

public interface WorkspaceService {
    BusinessDataVO getBusinessData(LocalDateTime begin, LocalDateTime end);

    SetmealOverviewVO getSetmealOverview();

    DishOverviewVO getDishOverview();

    OrderOverviewVO getOrderOverview();
}
