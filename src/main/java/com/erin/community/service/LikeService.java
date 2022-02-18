package com.erin.community.service;

import com.erin.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: erin
 * \* To change this template use File | Settings | Editor | File and Code Templates | File | Class.
 * \* Description: 处理点赞业务
 * \
 */

@Service
public class LikeService {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 点赞
     * 因为在这个业务中会连续执行两次更新的操作，所以整个业务应该保证事务性
     * @param userId 是点赞的用户的id（当前用户）
     * @param entityType
     * @param entityId
     */
    public void like(int userId, int entityType, int entityId, int entityUserId) {
        // 只处理对实体（帖子或评论）的点赞
        /*// 拼接当前实体的type和id作为Redis数据库中的键
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        // 查询用户是否已经点赞了（存在key对应的set中）
        boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey, userId);
        if (isMember) { // 已经点过赞了，将userId从set中去掉
            redisTemplate.opsForSet().remove(entityLikeKey, userId);
        } else {
            redisTemplate.opsForSet().add(entityLikeKey, userId);
        }*/

        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
                String userLikeKey = RedisKeyUtil.getUserLikeKey(entityUserId); // 被点赞的用户的id，即被赞的实体的作者

                // operations代替了redisTemplate,处理对实体（帖子或评论）的点赞,判断用户是否对当前实体点赞了
                // 查询操作要放在事务外
                boolean isMember = operations.opsForSet().isMember(entityLikeKey, userId);

                // 开启事务
                operations.multi();

                if (isMember) {
                    // 已经点赞了就在redis中取消点赞
                    operations.opsForSet().remove(entityLikeKey, userId);
                    // 实体收到的点赞减一
                    operations.opsForValue().decrement(userLikeKey);
                } else {
                    operations.opsForSet().add(entityLikeKey, userId);
                    operations.opsForValue().increment(userLikeKey);
                }

                // 执行事务
                return operations.exec();
            }
        });
    }

    /**
     * 查询某实体（帖子或评论）点赞的数量
     * @param entityType
     * @param entityId
     * @return
     */
    public long findEntityLikeCount(int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    /**
     * 查询某人对某实体的点赞状态，如果点赞了就是1，没点赞就是0，之后可能会扩展点踩功能
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    public int findEntityLikeStatus(int userId, int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().isMember(entityLikeKey, userId) ? 1 : 0;
    }

    /**
     * 查询某个用户(实体的作者)获得的赞数量
     * @param userId
     * @return
     */
    public int findUserLikeCount(int userId) {
        String userLikeKey = RedisKeyUtil.getUserLikeKey(userId);
        Integer count = (Integer) redisTemplate.opsForValue().get(userLikeKey);
        return count == null ? 0 : count.intValue();
    }

}

