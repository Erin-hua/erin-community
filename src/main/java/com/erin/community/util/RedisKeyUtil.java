package com.erin.community.util;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: erin
 * \* To change this template use File | Settings | Editor | File and Code Templates | File | Class.
 * \* Description: 设置Redis数据库中的key
 * \
 */

public class RedisKeyUtil {

    private static final String SPLIT = ":";
    // 设置实体的赞的key的前缀
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    // 设置用户得到的赞的key的前缀
    private static final String PREFIX_USER_LIKE = "like:user";
    // 用户关注的目标（用户、帖子、题目等）
    private static final String PREFIX_FOLLOWEE = "followee";
    // 将用户关注的目标作为key，将自己作为value存入redis数据库，方便统计关注的目标的粉丝数
    private static final String PREFIX_FOLLOWER = "follower";
    // 存登陆时需要的验证码
    private static final String PREFIX_KAPTCHA = "kaptcha";
    // 存登陆时用户的登陆凭证
    private static final String PREFIX_TICKET = "ticket";
    // 存当前登陆的用户
    private static final String PREFIX_USER = "user";

    /**
     * 某个实体(帖子或评论)得到的赞，哪个用户给某个实体点赞了就将这个userId作为value存到set中
     * like:entity:entityType:entityId -> set(userId)
     *
     * @param entityType
     * @param entityId
     * @return
     */
    public static String getEntityLikeKey(int entityType, int entityId) {
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    /**
     * 某个用户的得到的点赞数量
     * like:user:userId -> int
     *
     * @param userId
     * @return
     */
    public static String getUserLikeKey(int userId) {
        return PREFIX_USER_LIKE + SPLIT + userId;
    }

    /**
     * 某个用户（userId）关注的实体，有序集合中需要分数score来排序，在此用当前时间的整数（now）形式作为score
     * 注意，在Redis中是以（score,value）的形式存的数据，spring整合了Redis后给的添加数据的方法是(value,score)的形式
     * followee:userId:entityType -> zset(entityId,now)
     *
     * @param userId
     * @param entityType
     * @return
     */
    public static String getFolloweeKey(int userId, int entityType) {
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    /**
     * 某个实体（用户、帖子、题目等）拥有的粉丝
     * follower:entityType:entityId -> zset(userId,now)
     * @param entityType
     * @param entityId
     * @return
     */
    public static String getFollowerKey(int entityType, int entityId) {
        return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }

    /**
     * 登录验证码，因为用户访问登陆页面时还不知道这个用户的id，所以无法用用户的id作为唯一绑定验证码的值
     * 所以用户访问登陆页面时，服务器端向这个用户发送一个凭证（随即生成的字符串）并将其存到cookie中
     * 用这个凭证（字符串）来标识用户，而且这个临时凭证可以设置过期的时间
     *
     * @param owner 验证码属于哪个用户
     * @return
     */
    public static String getKaptchaKey(String owner) {
        return PREFIX_KAPTCHA + SPLIT + owner;
    }

    /**
     * 登录的凭证
     *
     * @param ticket 登陆成功的凭证
     * @return
     */
    public static String getTicketKey(String ticket) {
        return PREFIX_TICKET + SPLIT + ticket;
    }

    /**
     * 当前登陆的用户
     *
     * @param userId
     * @return
     */
    public static String getUserKey(int userId) {
        return PREFIX_USER + SPLIT + userId;
    }
}
