package com.crazy.service;

import com.crazy.vo.OrderReportVO;
import com.crazy.vo.SalesTop10ReportVO;
import com.crazy.vo.TurnoverReportVO;
import com.crazy.vo.UserReportVO;

import java.time.LocalDate;

public interface ReportService {
    TurnoverReportVO getTurnover(LocalDate begin, LocalDate end);

    UserReportVO getUserStatistics(LocalDate begin, LocalDate end);

    OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end);

    SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end);
}
