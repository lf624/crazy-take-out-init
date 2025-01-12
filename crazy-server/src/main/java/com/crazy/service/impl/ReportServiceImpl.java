package com.crazy.service.impl;

import com.crazy.constant.OrderStatus;
import com.crazy.dto.GoodsSalesTop10;
import com.crazy.mapper.OrderMapper;
import com.crazy.mapper.UserMapper;
import com.crazy.service.ReportService;
import com.crazy.service.WorkspaceService;
import com.crazy.vo.*;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    OrderMapper orderMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    WorkspaceService workspaceService;

    /**
     * 根据时间区间统计营业额
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO getTurnover(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = getDateList(begin, end);

        List<Double> turnoverList = new ArrayList<>();
        for(LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map<String, Object> map = new HashMap<>();
            map.put("status", OrderStatus.COMPLETED);
            map.put("begin", beginTime);
            map.put("end", endTime);
            Double turnover = orderMapper.sumByMap(map);
            turnover = turnover == null ? 0.0 : turnover;
            turnoverList.add(turnover);
        }

        // 使用 apache commons lang3 库的 StringUtils
        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();
    }

    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = getDateList(begin, end);

        List<Integer> newUserList = new ArrayList<>();
        List<Integer> totalUserList = new ArrayList<>();

        for(LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            // 新增用户数量 select count(id) from user where create_time > ? and create_time < ?
            Integer newUser = getUserCount(beginTime, endTime);
            // 总用户数量 select count(id) from user where  create_time < ?
            Integer totalUser = getUserCount(null, endTime);
            newUserList.add(newUser);
            totalUserList.add(totalUser);
        }

        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .build();
    }

    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = getDateList(begin, end);

        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();

        for(LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            Integer orderCount = getOrderCount(beginTime, endTime, null);
            Integer validOrderCount = getOrderCount(beginTime, endTime, OrderStatus.COMPLETED);
            orderCountList.add(orderCount);
            validOrderCountList.add(validOrderCount);
        }

        // 时间区间内的总订单数
        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).get();
        // 时间区间内的总有效订单数
        Integer validOrderCount = validOrderCountList.stream().reduce(Integer::sum).get();
        // 订单有效率
        double orderCompletionRate = 0.0;
        if(!totalOrderCount.equals(0)) {
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;
        }

        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .orderCountList(StringUtils.join(orderCountList, ","))
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    @Override
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        List<GoodsSalesTop10> salesTop10s = orderMapper.getSalesTop10(beginTime, endTime);

        String nameList = salesTop10s.stream()
                .map(GoodsSalesTop10::getName)
                .collect(Collectors.joining(","));
        String numberList = salesTop10s.stream()
                .map(s -> s.getNumber().toString())
                .collect(Collectors.joining(","));
        return SalesTop10ReportVO.builder()
                .nameList(nameList)
                .numberList(numberList)
                .build();
    }

    @Override
    public void exportBusinessData(HttpServletResponse response) {
        LocalDate begin = LocalDate.now().minusDays(30);
        LocalDate end = LocalDate.now().minusDays(1);
        // 查询概览运营数据
        BusinessDataVO totalBusinessData = workspaceService.getBusinessData(
                LocalDateTime.of(begin, LocalTime.MIN), LocalDateTime.of(end, LocalTime.MAX));
        try (InputStream input = this.getClass().getClassLoader().getResourceAsStream(
                "template/DataReportTemplate.xlsx")){ // 提供 Excel 模板文件
            if(input == null)
                throw new RuntimeException("Unable to load the DataReportTemplate.xlsx resource");
            // 基于提供好的模板文件创建一个新的Excel表格对象
            XSSFWorkbook excel = new XSSFWorkbook(input);
            // 获得Excel文件中的一个Sheet页
            XSSFSheet sheet = excel.getSheet("Sheet1");

            sheet.getRow(1).getCell(1).setCellValue(begin + "至" + end);
            sheet.getRow(3).getCell(2).setCellValue(totalBusinessData.getTurnover());
            sheet.getRow(3).getCell(4).setCellValue(totalBusinessData.getOrderCompletionRate());
            sheet.getRow(3).getCell(6).setCellValue(totalBusinessData.getNewUsers());
            sheet.getRow(4).getCell(2).setCellValue(totalBusinessData.getValidOrderCount());
            sheet.getRow(4).getCell(4).setCellValue(totalBusinessData.getUnitPrice());

            for(int i = 0; i < 30; i++) {
                LocalDate date = begin.plusDays(i);
                // 查询明细数据
                BusinessDataVO dayBusinessDate = workspaceService.getBusinessData(
                        LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));
                XSSFRow row = sheet.getRow(7 + i);
                row.getCell(1).setCellValue(date.toString());
                row.getCell(2).setCellValue(dayBusinessDate.getTurnover());
                row.getCell(3).setCellValue(dayBusinessDate.getValidOrderCount());
                row.getCell(4).setCellValue(dayBusinessDate.getOrderCompletionRate());
                row.getCell(5).setCellValue(dayBusinessDate.getUnitPrice());
                row.getCell(6).setCellValue(dayBusinessDate.getNewUsers());
            }
            // 通过输出流将文件下载到客户端浏览器中
            ServletOutputStream output = response.getOutputStream();
            excel.write(output);

            // 关闭资源
            output.flush();
            output.close();
            excel.close();
        } catch (IOException e) {
            log.error(e.toString());
        }
    }

    // 得到时间区间
    private List<LocalDate> getDateList(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);

        while (begin.isBefore(end)) {
            //日期计算，获得指定日期后1天的日期
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        return dateList;
    }

    private Integer getUserCount(LocalDateTime begin, LocalDateTime end) {
        Map<String, Object> map = new HashMap<>();
        map.put("begin", begin);
        map.put("end", end);
        return userMapper.countByMap(map);
    }

    private Integer getOrderCount(LocalDateTime begin, LocalDateTime end, Integer status) {
        Map<String, Object> map = new HashMap<>();
        map.put("status", status);
        map.put("begin", begin);
        map.put("end", end);
        return orderMapper.countByMap(map);
    }
}
