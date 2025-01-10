package com.crazy.mapper;

import com.crazy.dto.OrderSearchDTO;
import com.crazy.entity.Orders;
import com.crazy.vo.OrderDetailVO;
import com.crazy.vo.OrderSearchVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

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
}
