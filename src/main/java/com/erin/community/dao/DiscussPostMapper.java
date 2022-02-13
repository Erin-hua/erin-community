package com.erin.community.dao;

import com.erin.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: erin
 * \* To change this template use File | Settings | Editor | File and Code Templates | File | Interface.
 * \* Description: discuss_post表的mapper
 * \
 */

@Mapper
public interface DiscussPostMapper {

    /**
     * userId用于开发用户的个人主页中的功能：我发布的帖子
     * 首页查询的时候userId=0或者不存在，不将该参数拼接到sql语句中，这将是一个动态的sql
     * 考虑到首页分页的情况，offet是每一页起始行的行号，limit是每一页最多显示多少行数据
     * */
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);

    /**
     * 查询对应用户有多少条帖子
     * @Param注解用于给参数取别名,
     * 如果只有一个参数,并且在<if>里使用（动态sql）,则必须起别名
     * */
    int selectDiscussPostRows(@Param("userId") int userId);

    /**
     * 增加帖子
     * @param discussPost
     * @return 增加的行数
     */
    int insertDiscussPost(DiscussPost discussPost);

    /**
     * 根据帖子的id得到帖子
     * @param id
     * @return
     */
    DiscussPost selectDiscussPostById(int id);

    /**
     *
     * @param id 帖子的id
     * @param commentCount 更新的评论的数量
     * @return 更新的行数
     */
    int updateCommentCount(int id, int commentCount);
}
