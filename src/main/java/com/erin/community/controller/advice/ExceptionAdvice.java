package com.erin.community.controller.advice;

import com.erin.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: erin
 * \* To change this template use File | Settings | Editor | File and Code Templates | File | Class.
 * \* Description: 统一处理Controller中抛出的异常的全局配置类
 * \
 */

/**
 * @ControllerAdvice(annotations = Controller.class)表示只会扫描并处理带有@Controller注解的类
 *
 */
@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    /**
     * Controller产生异常后才会调用该方法
     * @param e Controller发生异常后会将Excpetion对象传给该方法
     * @param request
     * @param response
     * @throws IOException
     */
    @ExceptionHandler({Exception.class})
    public void handleException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.error("服务器发生异常: " + e.getMessage());
        // element记录了每个异常的信息
        for (StackTraceElement element : e.getStackTrace()) {
            logger.error(element.toString());
        }

        // 判断浏览器的请求是普通请求还是异步请求
        String xRequestedWith = request.getHeader("x-requested-with");
        // 当前请求是异步的请求（应该返回XML或JSON）
        if ("XMLHttpRequest".equals(xRequestedWith)) {
            // plain表示向浏览器返回的是普通的字符串对象，但该字符串可以是json格式的
            response.setContentType("application/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.write(CommunityUtil.getJSONString(1, "服务器异常!"));
        } else { // 当前请求是普通的请求（返回的是Html）
            // request.getContextPath()获得项目的访问路径
            response.sendRedirect(request.getContextPath() + "/error");
        }
    }

}

