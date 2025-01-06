package com.crazy.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.crazy.constant.MessageConstant;
import com.crazy.dto.UserLoginDTO;
import com.crazy.entity.User;
import com.crazy.exception.LoginFailedException;
import com.crazy.mapper.UserMapper;
import com.crazy.properties.WeChatProperties;
import com.crazy.service.UserService;
import com.crazy.utils.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    public static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    WeChatProperties weChatProperties;
    @Autowired
    UserMapper userMapper;

    @Override
    public User wxLogin(UserLoginDTO userLoginDTO) {
        String openid = getOpenId(userLoginDTO.getCode());

        if(openid == null)
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);

        User user = userMapper.getByOpenId(openid);

        if(user == null) {
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        }
        return user;
    }

    private String getOpenId(String code) {
        Map<String, String> query = new HashMap<>();
        query.put("appid", weChatProperties.getAppid());
        query.put("secret", weChatProperties.getSecret());
        query.put("js_code", code);
        query.put("grant_type", "authorization_code");

        String response = HttpClientUtil.doGet(WX_LOGIN, query);

        JSONObject jsonObject = JSONObject.parseObject(response);
        return jsonObject.getString("openid");
    }
}
