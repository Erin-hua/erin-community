package com.erin.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: erin
 * \* To change this template use File | Settings | Editor | File and Code Templates | File | Class.
 * \* Description: Redis测试类
 * \
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class RedisTests {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testStrings() {
        String redisKey = "test:count";

        redisTemplate.opsForValue().set(redisKey, 1);

        System.out.println(redisTemplate.opsForValue().get(redisKey)); // 1
        System.out.println(redisTemplate.opsForValue().increment(redisKey)); // 2
        System.out.println(redisTemplate.opsForValue().decrement(redisKey)); // 1
    }

    @Test
    public void testHashes() {
        String redisKey = "test:user";

        redisTemplate.opsForHash().put(redisKey, "id", 1);
        redisTemplate.opsForHash().put(redisKey, "username", "erin");

        System.out.println(redisTemplate.opsForHash().get(redisKey, "id"));
        System.out.println(redisTemplate.opsForHash().get(redisKey, "username"));
    }

    @Test
    public void testLists() {
        String redisKey = "test:ids";

        // 从列表左边往右边存数据
        redisTemplate.opsForList().leftPush(redisKey, 101);
        redisTemplate.opsForList().leftPush(redisKey, 102);
        redisTemplate.opsForList().leftPush(redisKey, 103);

        System.out.println(redisTemplate.opsForList().size(redisKey));
        // list的index是从左开始数的
        System.out.println(redisTemplate.opsForList().index(redisKey, 0));
        System.out.println(redisTemplate.opsForList().range(redisKey, 0, 2));

        System.out.println(redisTemplate.opsForList().leftPop(redisKey));
        System.out.println(redisTemplate.opsForList().leftPop(redisKey));
        System.out.println(redisTemplate.opsForList().leftPop(redisKey));
    }

    @Test
    public void testSets() {
        String redisKey = "test:teachers";

        redisTemplate.opsForSet().add(redisKey, "刘备", "关羽", "张飞", "赵云", "诸葛亮");

        System.out.println(redisTemplate.opsForSet().size(redisKey));
        System.out.println(redisTemplate.opsForSet().pop(redisKey)); // 随机
        System.out.println(redisTemplate.opsForSet().members(redisKey)); // 得到所有数据
    }

    @Test
    public void testSortedSets() {
        String redisKey = "test:students";

        redisTemplate.opsForZSet().add(redisKey, "唐僧", 80);
        redisTemplate.opsForZSet().add(redisKey, "悟空", 90);
        redisTemplate.opsForZSet().add(redisKey, "八戒", 150);
        redisTemplate.opsForZSet().add(redisKey, "沙僧", 70);
        redisTemplate.opsForZSet().add(redisKey, "白龙马", 60);

        System.out.println(redisTemplate.opsForZSet().zCard(redisKey)); // 统计有多少数据
        System.out.println(redisTemplate.opsForZSet().score(redisKey, "八戒")); // 统计某个人的分数score
        System.out.println(redisTemplate.opsForZSet().reverseRank(redisKey, "八戒")); // 从大到小排列得到某个人的排名
        System.out.println(redisTemplate.opsForZSet().reverseRange(redisKey, 0, 2));
    }

    @Test
    public void testKeys() {
        redisTemplate.delete("test:user");

        System.out.println(redisTemplate.hasKey("test:user"));

        // 指定国企时间为10秒
        redisTemplate.expire("test:students", 10, TimeUnit.SECONDS);
    }

    /**
     * 多次访问同一个key，批量发送命令,节约网络开销
     */
    @Test
    public void testBoundOperations() {
        String redisKey = "test:count";
        // BoundHashOperations等都可以，"hash"取决于要访问的value的类型
        BoundValueOperations operations = redisTemplate.boundValueOps(redisKey);
        operations.increment();
        operations.increment();
        operations.increment();
        operations.increment();
        operations.increment();
        System.out.println(operations.get());
    }

    /**
     * Redis支持事务，但机制不完全满足ACID特性，事务管理比较简单
     * 编程式事务示例，启用事务后，再执行Redis命令后不会立即执行，而是将其存到队列中，直到操作结束提交事务时，会将队列中所有的命令提交到Redis中统一批量执行
     * 在事务中间查询不会马上得到结果
     */
    @Test
    public void testTransaction() {
        Object result = redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String redisKey = "text:tx";

                // 启用事务
                redisOperations.multi();
                redisOperations.opsForSet().add(redisKey, "erin");
                redisOperations.opsForSet().add(redisKey, "lisa");
                redisOperations.opsForSet().add(redisKey, "sakura");

                System.out.println(redisOperations.opsForSet().members(redisKey)); // 得到key对应的数据value

                // 提交事务
                return redisOperations.exec();
            }
        });
        System.out.println(result);
    }

}
