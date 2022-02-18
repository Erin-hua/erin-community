package com.erin.community.controller;

import com.erin.community.annotation.LoginRequired;
import com.erin.community.entity.User;
import com.erin.community.service.LikeService;
import com.erin.community.util.CommunityUtil;
import com.erin.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: erin
 * \* To change this template use File | Settings | Editor | File and Code Templates | File | Class.
 * \* Description:
 * \
 */

@Controller
public class LikeController {

    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    /**
     * 该点赞功能的访问是在帖子详情页面中实现的（有三个地方：给帖子点赞、给帖子的评论点赞、给帖子的评论的评论点赞）
     * 因为是异步请求所以需要注解@ResponseBody
     * 注解@LoginRequired表示访问这个路径需要用户登陆
     *
     * @param entityType
     * @param entityId
     * @return
     */
    //@LoginRequired
    @RequestMapping(path = "/like", method = RequestMethod.POST)
    @ResponseBody
    public String like(int entityType, int entityId, int entityUserId) {
        User user = hostHolder.getUser();

        // 调用点赞，内部逻辑会判断用户是否已经点赞了，如果点赞了则会取消点赞，如果没点赞就设置为点赞状态
        likeService.like(user.getId(), entityType, entityId, entityUserId);

        // 点赞的数量
        long likeCount = likeService.findEntityLikeCount(entityType, entityId);
        // 点赞的状态，如果是1则表示点赞了，如果是0表示没有点赞
        int likeStatus = likeService.findEntityLikeStatus(user.getId(), entityType, entityId);
        // 返回给客户端的结果
        Map<String, Object> map = new HashMap<>();
        map.put("likeCount", likeCount);
        map.put("likeStatus", likeStatus);

        return CommunityUtil.getJSONString(0, null, map);
    }

}

