package com.erin.community.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: erin
 * \* To change this template use File | Settings | Editor | File and Code Templates | File | Class.
 * \* Description: 演示一下AOP
 * \
 */

// Aspect注解表示该类是方面组件
// @Component
// @Aspect
public class ErinAspect {

    /**
     * 定义切点，说明哪些bean哪些方法是需要处理的目标
     * 第一个*表示方法的返回值可以是任何类型
     * com.erin.community.service.*.*中第一个*代表对应包下的所有业务组件，第二个*表示所有的方法，(..)表示所有的参数
     */
    @Pointcut("execution(* com.erin.community.service.*.*(..))")
    public void pointcut() {

    }

    /**
     * 在切点前织入（通知）代码
     */
    @Before("pointcut()")
    public void before() {
        System.out.println("before");
    }

    /**
     * 在切点后织入（通知）代码
     */
    @After("pointcut()")
    public void after() {
        System.out.println("after");
    }

    /**
     * 在有返回值之后织入（通知）代码
     */
    @AfterReturning("pointcut()")
    public void afterRetuning() {
        System.out.println("afterRetuning");
    }

    /**
     * 在抛出异常后织入（通知）代码
     */
    @AfterThrowing("pointcut()")
    public void afterThrowing() {
        System.out.println("afterThrowing");
    }

    /**
     * 在前后都能织入（通知）代码
     */
    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("around before"); // 在调用目标组件之前织入的代码
        Object obj = joinPoint.proceed(); // 调用目标组件的方法，obj是其返回值
        System.out.println("around after"); // 在调用目标组件之后织入的代码
        return obj;
    }

}
