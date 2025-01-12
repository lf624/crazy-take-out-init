package com.crazy.mapper;

import com.crazy.dto.GoodsSalesTop10;
import com.crazy.dto.OrderSearchDTO;
import com.crazy.entity.Orders;
import com.crazy.vo.OrderDetailVO;
import com.crazy.vo.OrderSearchVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {

    void insert(Orders order);

    @Select("SELECT * FROM orders WHERE number = #{number} AND user_id = #{userId}")
    Orders getByNumberAndUserId(String number, Long userId);

    void update(Orders order);

    @Select("SELECT * FROM orders WHERE id = #{id}")
    Orders getById(Long id);

    List<OrderDetailVO> page(Integer status, int offset, int limit);

    Page<OrderSearchVO> conditionSearch(OrderSearchDTO orderSearchDTO);

    @Select("SELECT count(id) FROM orders WHERE status = #{status}")
    int countByStatus(int status);

    @Select("SELECT * FROM orders WHERE status = #{status} AND order_time < #{orderTime}")
    List<Orders> getByStatusAndOrderTimeLT(Integer status, LocalDateTime orderTime);

    Double sumByMap(Map<String, Object> map);

    Integer countByMap(Map<String, Object> map);

    List<GoodsSalesTop10> getSalesTop10(LocalDateTime begin, LocalDateTime end);
}
