package com.erin.community.service;

import com.erin.community.dao.LoginTicketMapper;
import com.erin.community.dao.UserMapper;
import com.erin.community.entity.LoginTicket;
import com.erin.community.entity.User;
import com.erin.community.util.CommunityConstant;
import com.erin.community.util.CommunityUtil;
import com.erin.community.util.MailClient;
import com.erin.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: erin
 * \* To change this template use File | Settings | Editor | File and Code Templates | File | Class.
 * \* Description: user表对应的业务类
 */

@Service
public class UserService implements CommunityConstant {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    // Thymeleaf模板引擎的核心类，负责生成动态网页
    @Autowired
    private TemplateEngine templateEngine;

    // 发邮件时要发送激活码，要包括域名以及项目名
    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    /*@Autowired
    private LoginTicketMapper loginTicketMapper;*/

    @Autowired
    private RedisTemplate redisTemplate;

    public User findUserById(int id) {
        // 被重构了
        /*return userMapper.selectById(id);*/
        User user = getCache(id);
        if (user == null) {
            user = initCache(id);
        }
        return user;
    }

    // user是页面传入的
    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>();

        // 空值处理
        if (user == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }
        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg", "账号不能为空!");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "密码不能为空!");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg", "邮箱不能为空!");
            return map;
        }

        // 通过用户名来验证账号是否已經存在
        User u = userMapper.selectByName(user.getUsername());
        if (u != null) {
            map.put("usernameMsg", "该账号已存在!");
            return map;
        }

        // 通过邮箱来验证用户是否已经存在
        u = userMapper.selectByEmail(user.getEmail());
        if (u != null) {
            map.put("emailMsg", "该邮箱已被注册!");
            return map;
        }

        // 注册用户
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        // 加密密码
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        // 默认普通用户
        user.setType(0);
        // 默认没有被激活
        user.setStatus(0);
        // 激活码
        user.setActivationCode(CommunityUtil.generateUUID());
        // 设置随机头像，%d是占位符
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        // 在此之前user没有id，是mybatis从数据库中获取对应的id然后回填了的
        userMapper.insertUser(user);

        // 以html形式发送激活邮件
        // 把要传给模板的变量存到context对象中
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        // 激活路径示例：http://localhost:8080/community/activation/101/code
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);

        // 调用模板引擎生成动态网页(其实是字符串)
        String content = templateEngine.process("/mail/activation", context);

        // 客户端委托服务端去发送HTML激活邮件
        mailClient.sendMail(user.getEmail(), "激活账号", content);

        // 如果为空表示没有问题出现
        return map;
    }

    /**
    * 用户点击激活链接，服务端帮忙将用户的状态改为已激活：1，缓存的用户信息也需要删除
    * */
    public int activation(int userId, String code) {
        User user = userMapper.selectById(userId);
        if (user.getStatus() == 1) {
            return ACTIVATION_REPEAT;
        } else if (user.getActivationCode().equals(code)) {
            userMapper.updateStatus(userId, 1);
            // 删除缓存了的用户的信息，因为用户的信息数据变更了（状态改为1了）
            clearCache(userId);
            return ACTIVATION_SUCCESS;
        } else {
            return ACTIVATION_FAILURE;
        }
    }

    // expiredSeconds表示多少秒后凭证过期
    public Map<String, Object> login(String username, String password, int expiredSeconds) {
        Map<String, Object> map = new HashMap<>();

        // 空值处理
        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg", "账号不能为空!");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空!");
            return map;
        }

        // 验证账号
        User user = userMapper.selectByName(username);
        if (user == null) {
            map.put("usernameMsg", "该账号不存在!");
            return map;
        }

        // 验证状态
        if (user.getStatus() == 0) { // 注册了但没激活
            map.put("usernameMsg", "该账号未激活!");
            return map;
        }

        // 验证密码
        password = CommunityUtil.md5(password + user.getSalt());
        if (!user.getPassword().equals(password)) {
            map.put("passwordMsg", "密码不正确!");
            return map;
        }

        // 生成登录凭证然后insert到login_ticket表中，该表相当于起到了session的作用，但下面优化了，存到了Redis数据库中
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
        // loginTicketMapper.insertLoginTicket(loginTicket);
        // 不将用户登陆成功的凭证存到Mysql数据库中，而是存到Redis数据库中
        String redisKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        // Redis会将loginTicket序列化为一个json字符串来保存
        redisTemplate.opsForValue().set(redisKey, loginTicket);

        map.put("ticket", loginTicket.getTicket());
        return map;
    }

    public void logout(String ticket) {
        /*loginTicketMapper.updateStatus(ticket, 1);*/
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(redisKey);
        loginTicket.setStatus(1); // 表示删除态
        redisTemplate.opsForValue().set(redisKey, loginTicket);
    }

    public LoginTicket findLoginTicket(String ticket) {
        /*return loginTicketMapper.selectByTicket(ticket);*/
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(redisKey);
    }

    public int updateHeader(int userId, String headerUrl) {
        /*return userMapper.updateHeader(userId, headerUrl);*/
        // 用户状态被修改，需要清理缓存
        int rows = userMapper.updateHeader(userId, headerUrl);
        clearCache(userId);
        return rows;
    }

    public User findUserByName(String username) {
        return userMapper.selectByName(username);
    }

    // 对用户信息进行缓存的三个步骤
    // 1.优先从缓存中取值
    private User getCache(int userId) {
        String redisKey = RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(redisKey);
    }

    // 2.取不到时初始化缓存数据
    private User initCache(int userId) {
        User user = userMapper.selectById(userId);
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(redisKey, user, 3600, TimeUnit.SECONDS);
        return user;
    }

    // 3.数据变更时清除缓存数据
    private void clearCache(int userId) {
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(redisKey);
    }
}
