package com.erin.community.controller.interceptor;

import com.erin.community.entity.LoginTicket;
import com.erin.community.entity.User;
import com.erin.community.service.UserService;
import com.erin.community.util.CookieUtil;
import com.erin.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: erin
 * \* To change this template use File | Settings | Editor | File and Code Templates | File | Class.
 * \* Description: 拦截器，用于在请求之前、之后、模板渲染成功后的时候处理数据，然后在配置类中注册该拦截器
 * \
 */

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    /*
    * 在请求开始前通过凭证找到用户，并且把用户暂存到当前线程对应的对象中
    *
    * */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从cookie中获取用户登陆的凭证ticket
        String ticket = CookieUtil.getValue(request, "ticket");

        if (ticket != null) {
            // 查询凭证
            LoginTicket loginTicket = userService.findLoginTicket(ticket);
            // 检查凭证是否有效，status为0表示用户登陆处于成功的状态
            if (loginTicket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())) {
                // 根据凭证查询用户
                User user = userService.findUserById(loginTicket.getUserId());
                // 在本次请求中持有用户，把数据存入到当前线程对应的map中，如果请求没有处理完，线程就一直存在
                hostHolder.setUser(user);
            }
        }

        return true;
    }

    /*
    * 因为在渲染页面之前需要得到当前用户的信息，所以要在模板引擎执行之前需要将暂存的user信息添加到model中
    *
    * */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if (user != null && modelAndView != null) {
            modelAndView.addObject("loginUser", user);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
    }
}
