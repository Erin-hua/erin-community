package com.erin.community.dao;

import com.erin.community.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: erin
 * \* To change this template use File | Settings | Editor | File and Code Templates | File | Interface.
 * \* Description: user表的mapper
 * \
 */

@Mapper
public interface UserMapper {

    User selectById(int id);

    User selectByName(String username);

    User selectByEmail(String email);

    int insertUser(User user);

    int updateStatus(int id, int status);

    // 更新图像路径
    int updateHeader(int id, String headerUrl);

    int updatePassword(int id, String password);

}

