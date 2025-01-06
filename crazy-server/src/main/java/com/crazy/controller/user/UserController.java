package com.crazy.controller.user;

import com.crazy.constant.JwtClaimsConstant;
import com.crazy.context.BaseContext;
import com.crazy.dto.UserLoginDTO;
import com.crazy.entity.User;
import com.crazy.properties.JwtProperties;
import com.crazy.result.Result;
import com.crazy.service.UserService;
import com.crazy.utils.JwtUtil;
import com.crazy.vo.UserLoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user/user")
@Tag(name = "C端用户相关接口")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtProperties jwtProperties;

    @PostMapping("/login")
    @Operation(summary = "微信登陆")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO) {
        log.info("wx user login: {}", userLoginDTO.getCode());

        User user = userService.wxLogin(userLoginDTO);

        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, user.getId());
        String token = JwtUtil.createJwt(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claims);

        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getId())
                .openid(user.getOpenid())
                .token(token)
                .build();
        return Result.success(userLoginVO);
    }

    @PostMapping("/logout")
    @Operation(summary = "退出登陆")
    public Result<String> logout() {
        BaseContext.removeCurrentId();
        return Result.success();
    }
}
