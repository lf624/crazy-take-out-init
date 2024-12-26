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
public class JwtTokenAdminInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 校验 jwt
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // HandlerMethod 表示基于注解的控制器（如 @Controller 或 @RestController）处理时
        if(!(handler instanceof HandlerMethod)) {
            // 其他处理器或静态资源，不处理
            return true;
        }

        // 从请求头中获取令牌并验证
        String token = request.getHeader(jwtProperties.getAdminTokenName());
        try {
            log.info("jwt checkout: {}", token);
            Map<String, Object> claims = JwtUtil.parseJwt(jwtProperties.getAdminSecretKey(), token);
            Long empId = Long.valueOf(claims.get(JwtClaimsConstant.EMP_ID).toString());
            log.info("current employee Id: {}", empId);
            // 将用户Id放入ThreadLocal
            BaseContext.setCurrentId(empId);
            return true;
        } catch (Exception e) {
            log.info("checkout failed.");
            response.setStatus(401);
            return false;
        }
    }
}
