package com.crazy.interceptor;

import com.crazy.constant.JwtClaimsConstant;
import com.crazy.context.BaseContext;
import com.crazy.properties.JwtProperties;
import com.crazy.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Slf4j
@Component
public class JwtTokenUserInterceptor implements HandlerInterceptor {
    @Autowired
    JwtProperties jwtProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 检查拦截的是否是 controller 中的方法
        if(!(handler instanceof HandlerMethod)) {
            // 直接放行
            return true;
        }

        String token = request.getHeader(jwtProperties.getUserTokenName());

        try {
            log.info("jwt checkout: {}", token);
            Map<String, Object> claims = JwtUtil.parseJwt(jwtProperties.getUserSecretKey(), token);
            Long userId = Long.valueOf(claims.get(JwtClaimsConstant.USER_ID).toString());
            log.info("current user id: {}", userId);
            BaseContext.setCurrentId(userId);
            return true;
        } catch (Exception e) {
            log.info("checkout failed.");
            response.setStatus(401);
            return false;
        }
    }
}
