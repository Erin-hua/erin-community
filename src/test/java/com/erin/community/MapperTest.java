package com.erin.community;

import com.erin.community.dao.DiscussPostMapper;
import com.erin.community.dao.UserMapper;
import com.erin.community.entity.DiscussPost;
import com.erin.community.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTest {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Test
    public void testSelectUser() {
        User user = userMapper.selectById(103);
        System.out.println(user);

        user = userMapper.selectByName("zhangfei");
        System.out.println(user);

        user = userMapper.selectByEmail("nowcoder103@sina.com");
        System.out.println(user);
    }

    @Test
    public void testInsertUser() {
        // 不用传id，因为数据库可以自动生成
        User user = new User();
        user.setUsername("erin");
        user.setPassword("hua123456");
        user.setSalt("sakura");
        user.setEmail("ErinSakura@gmail.com");
        // 0-1000 png
        user.setHeaderUrl("http://www.nowcoder.com/100.png");
        user.setCreateTime(new Date());

        int rows = userMapper.insertUser(user);
        System.out.println(rows);
        System.out.println(user.getId());
    }

    @Test
    public void updateUser() {
        // 返回的row是修改了几行，status为1表示已激活
        int rows = userMapper.updateStatus(150, 1);
        System.out.println(rows);

        rows = userMapper.updateHeader(150, "http://www.nowcoder.com/105.png");
        System.out.println(rows);

        rows = userMapper.updatePassword(150, "helloErin");
        System.out.println(rows);
    }

    @Test
    public void testSelectPosts() {
        // 不按用户id查询
        // List<DiscussPost> list = discussPostMapper.selectDiscussPosts(0, 0, 10);

        // 按用户id查询
        List<DiscussPost> list = discussPostMapper.selectDiscussPosts(138, 0, 10);
        for(DiscussPost post : list) {
            System.out.println(post);
        }

        // 查询一共有多少条帖子数据
        // int rows = discussPostMapper.selectDiscussPostRows(0);
        // 按照用户id查询帖子数量
        int rows = discussPostMapper.selectDiscussPostRows(138);
        System.out.println(rows);
    }
}
