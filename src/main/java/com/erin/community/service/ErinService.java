package com.erin.community.service;

import com.erin.community.dao.DiscussPostMapper;
import com.erin.community.dao.ErinDao;
import com.erin.community.dao.UserMapper;
import com.erin.community.entity.DiscussPost;
import com.erin.community.entity.User;
import com.erin.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Date;

// 和实际项目开发无关
@Service
// 指定为原型模式而不是默认的单例模式，这种多实例的情况比较少见
// @Scope("prototype")
public class ErinService {

    @Autowired
    private ErinDao erinDao;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    // 编程式事务可以通过TransactionTemplate管理事务，并通过它执行数据库的操作
    @Autowired
    private TransactionTemplate transactionTemplate;

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

    /**
     * 声明式事务示例，通过注解，声明某方法的事务特征：该事务由新增用户以及新增的用户新增帖子事件组成
     * isolation 用于指定隔离级别，当前级别指定的是READ_COMMITTED
     * propagation 指定事务的传播机制，业务方法A可能会调用业务方法B，这两个方法都可能要管理事务，事务传播机制就是解决事务交叉处理问题
     * REQUIRED: 方法A调用当前方法B，则对B而言A就是当前事务（外部事务），支持当前事务(外部事务)，如果当前事务(外部事务)不存在，则当前方法B自己创建新事务.
     * REQUIRES_NEW: 创建一个新事务，并且暂停当前事务(外部事务)，A调用B，B会无视A的事务
     * NESTED: 如果当前存在事务(外部事务)，则嵌套在该事务中执行(B的事务执行的时候会有独立的提交和回滚)，如果当前事务(外部事务)不存在，则就会REQUIRED一样
     *
     * @return
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public Object save1() {
        // 新增用户
        User user = new User();
        user.setUsername("erin1999");
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5("789" + user.getSalt()));
        user.setEmail("erin@qq.com");
        user.setHeaderUrl("http://image.nowcoder.com/head/98t.png");
        user.setCreateTime(new Date());
        // 执行该语句后，MyBatis会向数据库索取user的id，然后将id回填给user
        userMapper.insertUser(user);

        // 新增帖子
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle("Hello");
        post.setContent("新人报道!");
        post.setCreateTime(new Date());
        discussPostMapper.insertDiscussPost(post);

        // 这里报错然后当前事务就会回滚，新增用户和新增帖子就会失败，不会往数据库中存数据
        Integer.valueOf("abc");

        return "ok";
    }

    /**
     * 编程式事务：通过TransactionTemplate管理事务，并通过它执行数据库的操作
     *
     * @return
     */
    public Object save2() {
        // 设置事务隔离级别
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        // 设置传播机制
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        return transactionTemplate.execute(new TransactionCallback<Object>() {
            /**
             * transactionTemplate底层会自动调用该方法（回调方法），只需要定义该方法中的逻辑
             * @param status
             * @return
             */
            @Override
            public Object doInTransaction(TransactionStatus status) {
                // 新增用户
                User user = new User();
                user.setUsername("sana");
                user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
                user.setPassword(CommunityUtil.md5("760" + user.getSalt()));
                user.setEmail("sana@qq.com");
                user.setHeaderUrl("http://image.nowcoder.com/head/99t.png");
                user.setCreateTime(new Date());
                // 执行该语句后，MyBatis会向数据库索取user的id，然后将id回填给user
                userMapper.insertUser(user);

                // 新增帖子
                DiscussPost post = new DiscussPost();
                post.setUserId(user.getId());
                post.setTitle("Hello");
                post.setContent("菜鸟一枚");
                post.setCreateTime(new Date());
                discussPostMapper.insertDiscussPost(post);

                // 这里报错然后当前事务就会回滚，新增用户和新增帖子就会失败，不会往数据库中存数据
                Integer.valueOf("abc");

                return "ok";
            }
        });
    }
}
