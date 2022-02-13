package com.erin.community.controller.interceptor;

import com.erin.community.annotation.LoginRequired;
import com.erin.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: erin
 * \* To change this template use File | Settings | Editor | File and Code Templates | File | Class.
 * \* Description: 拦截带有@LoginRequired注解的方法、属性、类，用户如果没登陆就需要拦截，注意拦截器开发后要配置拦截器起作用的路径
 * \
 */

@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 判断要拦截的目标是不是一个方法，如果是则处理，不是就不处理，HandlerMethod是Spring MVC提供的类型
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            // 获取拦截到的method对象
            Method method = handlerMethod.getMethod();
            LoginRequired loginRequired = method.getAnnotation(LoginRequired.class);
            // 拦截到的方法需要登陆（loginRequired），但当前用户没有登陆则return false，拒绝用户后续请求，并强制重定向到登陆页面
            if (loginRequired != null && hostHolder.getUser() == null) {
                // controller中`return "redirect:/index"`类似语句的底层实现如下
                response.sendRedirect(request.getContextPath() + "/login");
                return false;
            }
        }
        return true;
    }
}

