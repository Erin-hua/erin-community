package com.erin.community.dao;

import org.springframework.stereotype.Repository;

// 与开发项目无关
@Repository("erinDaoImpl")
public class ErinDaoImpl implements ErinDao{
    @Override
    public String select() {
        return "Erin sakura";
    }
}
