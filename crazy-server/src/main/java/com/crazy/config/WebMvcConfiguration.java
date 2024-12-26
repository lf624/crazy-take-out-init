package com.crazy.config;

import com.crazy.interceptor.JwtTokenAdminInterceptor;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfiguration {
    @Autowired
    JwtTokenAdminInterceptor jwtTokenAdminInterceptor;

    /**
     * 注册Interceptor，设置静态资源映射
     * @return
     */
    @Bean
    WebMvcConfigurer createWebMvcConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(jwtTokenAdminInterceptor)
                        .addPathPatterns("/admin/**")
                        .excludePathPatterns("/admin/employee/login");
            }

            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                registry.addResourceHandler("/doc.html")
                        .addResourceLocations("classpath:/META-INF/resources/");
                registry.addResourceHandler("/webjars/**")
                        .addResourceLocations("classpath:/META-INF/resources/webjars/");
            }
        };
    }

    @Bean
    public OpenAPI publicApi() {
        return new OpenAPI()
                //.servers(serverList())
                .info(new Info()
                        .title("疯狂外卖项目接口文档")
                        //.extensions(Map.of("x-audience", "external-partner", "x-application-id", "APP-12345"))
                        .description("疯狂外卖项目接口文档")
                        .version("2.0")
                );
        //.addSecurityItem(new SecurityRequirement().addList("bearer-jwt", Arrays.asList("read", "write"))).security(securityList());
    }
}
