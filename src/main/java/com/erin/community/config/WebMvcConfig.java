package com.erin.community.config;

import com.erin.community.controller.interceptor.ErinInterceptor;
//import com.erin.community.controller.interceptor.LoginTicketInterceptor;
import com.erin.community.controller.interceptor.LoginRequiredInterceptor;
import com.erin.community.controller.interceptor.LoginTicketInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: erin
 * \* To change this template use File | Settings | Editor | File and Code Templates | File | Class.
 * \* Description: 拦截器的配置类，需要实现一个接口，而不是和普通的配置类一样装配bean
 * \
 */

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private ErinInterceptor erinInterceptor;

    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;

    @Autowired
    private LoginRequiredInterceptor loginRequiredInterceptor;

    /**
    * Sprng在调用该方法时会传入参数registry，这样方法就能注册interceptor进行拦截
    * */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 不用处理所有目录下的css、js、png、jpg、jpeg
        registry.addInterceptor(erinInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg")
                .addPathPatterns("/register", "/login");

        registry.addInterceptor(loginTicketInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg");

        registry.addInterceptor(loginRequiredInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg");
    }

}
