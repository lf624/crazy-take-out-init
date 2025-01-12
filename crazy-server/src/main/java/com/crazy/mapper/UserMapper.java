package com.crazy.mapper;

import com.crazy.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.Map;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM user WHERE openid = #{openid}")
    User getByOpenId(String openid);

    void insert(User user);

    Integer countByMap(Map<String, Object> map);
}
