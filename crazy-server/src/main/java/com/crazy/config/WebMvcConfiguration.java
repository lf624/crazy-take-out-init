package com.crazy.config;

import com.crazy.interceptor.JwtTokenAdminInterceptor;
import com.crazy.interceptor.JwtTokenUserInterceptor;
import com.crazy.json.JacksonObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.LocalDateTime;

@Slf4j
@Configuration
public class WebMvcConfiguration {
    @Autowired
    JwtTokenAdminInterceptor jwtTokenAdminInterceptor;
    @Autowired
    JwtTokenUserInterceptor jwtTokenUserInterceptor;

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
                        .excludePathPatterns("/admin/employee/login");// 登陆时不验证token
                registry.addInterceptor(jwtTokenUserInterceptor)
                        .addPathPatterns("/user/**")
                        .excludePathPatterns("/user/user/login")
                        .excludePathPatterns("/user/shop/status");
            }

            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                registry.addResourceHandler("/doc.html")
                        .addResourceLocations("classpath:/META-INF/resources/");
                registry.addResourceHandler("/webjars/**")
                        .addResourceLocations("classpath:/META-INF/resources/webjars/");
            }

//            // 扩展消息转换器
//            @Override
//            public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
//                log.info("extend message convertors...");
//                // 注意，如果直接创建一个MappingJackson2HttpMessageConverter，并插到最前面会影响Swagger3
//                for (HttpMessageConverter<?> converter : converters) {
//                    if (converter instanceof MappingJackson2HttpMessageConverter) {
//                        // 设置自定义 ObjectMapper
//                        ((MappingJackson2HttpMessageConverter) converter).setObjectMapper(new JacksonObjectMapper());
//                    }
//                }
//            }
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

    // 直接配置全局 ObjectMapper，会自动注入到默认的 MappingJackson2HttpMessageConverter 中，不用扩展消息转换器
    @Bean
    public ObjectMapper objectMapper() {
        return new JacksonObjectMapper();
    }
}
