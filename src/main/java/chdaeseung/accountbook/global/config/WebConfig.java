package chdaeseung.accountbook.global.config;

import chdaeseung.accountbook.global.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//@Configuration
//public class WebConfig implements WebMvcConfigurer {
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new LoginInterceptor())
//                .addPathPatterns(
//                        "/transactions/**",
//                        "/categories/**",
//                        "/dashboard",
//                        "/recurring/**",
//                        "/bank-account/**"
//                )
//                .excludePathPatterns(
//                        "/users/login",
//                        "/users/signup",
//                        "/css/**",
//                        "/js/**"
//                );
//    }
//}