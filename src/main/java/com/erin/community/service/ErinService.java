package com.erin.community.service;

import com.erin.community.dao.ErinDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

// 和实际项目开发无关
@Service
// 指定为原型模式而不是默认的单例模式，这种多实例的情况比较少见
// @Scope("prototype")
public class ErinService {

    @Autowired
    private ErinDao erinDao;

    public ErinService() {
        System.out.println("实例化ErinService");
    }

    // 表示该初始化方法会在构造器之后调用
    @PostConstruct
    public void init() {
        System.out.println("初始化ErinService");
    }

    // 表示在销毁对象之前调用该方法
    @PreDestroy
    public void destroy() {
        System.out.println("销毁ErinSerivce");
    }

    public String find() {
        return erinDao.select();
    }
}
