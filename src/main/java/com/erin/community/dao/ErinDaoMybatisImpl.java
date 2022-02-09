package com.erin.community.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

// 与开发项目无关
@Repository
@Primary
public class ErinDaoMybatisImpl implements ErinDao{
    @Override
    public String select() {
        return "Mybatis";
    }
}
