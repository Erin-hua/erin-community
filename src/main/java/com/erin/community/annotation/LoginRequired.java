package com.erin.community.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: erin
 * \* To change this template use File | Settings | Editor | File and Code Templates | File | Interface.
 * \* Description: 自定义注解
 * \
 */

// 这个注解要写在方法之上，如果方法a有这个注解，说明需要用户登陆才能访问方法a
@Target(ElementType.METHOD)
// 这个注解只在程序运行时有效
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginRequired {

}

