package com.erin.community.service;

import com.erin.community.dao.UserMapper;
import com.erin.community.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: erin
 * \* To change this template use File | Settings | Editor | File and Code Templates | File | Class.
 * \* Description: user表对应的业务类* \
 */

/*根据用户*/
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public User findUserById(int id) {
        return userMapper.selectById(id);
    }

}
