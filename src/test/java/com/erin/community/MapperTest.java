package com.erin.community;

import com.erin.community.dao.DiscussPostMapper;
import com.erin.community.dao.LoginTicketMapper;
import com.erin.community.dao.MessageMapper;
import com.erin.community.dao.UserMapper;
import com.erin.community.entity.DiscussPost;
import com.erin.community.entity.LoginTicket;
import com.erin.community.entity.Message;
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

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private MessageMapper messageMapper;

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

    /**
    * 测试：往数据库中插入登陆凭证
    * */
    @Test
    public void testInsertLoginTicket() {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("abc");
        loginTicket.setStatus(0); // 0-登陆凭证是有效的，表示登陆成功，1表示退出了
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 * 10)); // 1000 ms*60*10 = 10 min

        loginTicketMapper.insertLoginTicket(loginTicket);
    }

    /**
    * 测试： 根据登陆凭证查询数据库中对应的数据，并且修改其状态
    * */
    @Test
    public void testSelectLoginTicket() {
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket);

        loginTicketMapper.updateStatus("abc", 1);
        loginTicket = loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket);
    }

    @Test
    public void testSelectLetters() {
        // 查询某个用户一共有多少个会话（查询得到的是每个会话最新的私信）
        List<Message> list = messageMapper.selectConversations(111, 0, 20);
        for (Message message : list) {
            System.out.println(message);
        }

        int count = messageMapper.selectConversationCount(111);
        System.out.println(count);

        // 查询得到某个会话之下的所有私信
        list = messageMapper.selectLetters("111_112", 0, 10);
        for (Message message : list) {
            System.out.println(message);
        }

        count = messageMapper.selectLetterCount("111_112");
        System.out.println(count);

        count = messageMapper.selectLetterUnreadCount(131, "111_131");
        System.out.println(count);

    }

}
