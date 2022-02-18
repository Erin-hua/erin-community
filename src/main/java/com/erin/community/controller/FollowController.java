package com.erin.community.controller;

import com.erin.community.annotation.LoginRequired;
import com.erin.community.entity.Page;
import com.erin.community.entity.User;
import com.erin.community.service.FollowService;
import com.erin.community.service.UserService;
import com.erin.community.util.CommunityConstant;
import com.erin.community.util.CommunityUtil;
import com.erin.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: erin
 * \* To change this template use File | Settings | Editor | File and Code Templates | File | Class.
 * \* Description: 处理关注和取消关注的请求
 * \
 */

@Controller
public class FollowController implements CommunityConstant {

    @Autowired
    private FollowService followService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    /**
     * 异步的请求，在页面上点击关注时页面不会跳转
     * @param entityType
     * @param entityId
     * @return
     */
    @LoginRequired
    @ResponseBody
    @RequestMapping(path = "/follow", method = RequestMethod.POST)
    public String follow(int entityType, int entityId) {
        User user = hostHolder.getUser();

        followService.follow(user.getId(), entityType, entityId);

        return CommunityUtil.getJSONString(0, "已关注!");
    }

    @LoginRequired
    @ResponseBody
    @RequestMapping(path = "/unfollow", method = RequestMethod.POST)
    public String unfollow(int entityType, int entityId) {
        User user = hostHolder.getUser();

        followService.unfollow(user.getId(), entityType, entityId);

        return CommunityUtil.getJSONString(0, "已取消关注!");
    }

    @RequestMapping(path = "/followees/{userId}", method = RequestMethod.GET)
    public String getFollowees(@PathVariable("userId") int userId, Page page, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在!");
        }
        // 用于显示“某某关注的人”字符串中的某某
        model.addAttribute("user", user);

        page.setLimit(5);
        page.setPath("/followees/" + userId);
        // 当前用户（不一定是当前登陆的用户）一共关注了多少人，注意强制转换long类型为int类型
        page.setRows((int) followService.findFolloweeCount(userId, ENTITY_TYPE_USER));

        List<Map<String, Object>> userList = followService.findFollowees(userId, page.getOffset(), page.getLimit());
        if (userList != null) {
            for (Map<String, Object> map : userList) {
                User u = (User) map.get("user");
                // 补充当前登陆用户对 这个人的关注列表中 关注的某个人 的关注状态
                map.put("hasFollowed", hasFollowed(u.getId()));
            }
        }
        model.addAttribute("users", userList);

        return "/site/followee";
    }

    @RequestMapping(path = "/followers/{userId}", method = RequestMethod.GET)
    public String getFollowers(@PathVariable("userId") int userId, Page page, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在!");
        }
        model.addAttribute("user", user);

        page.setLimit(5);
        page.setPath("/followers/" + userId);
        page.setRows((int) followService.findFollowerCount(ENTITY_TYPE_USER, userId));

        List<Map<String, Object>> userList = followService.findFollowers(userId, page.getOffset(), page.getLimit());
        if (userList != null) {
            for (Map<String, Object> map : userList) {
                User u = (User) map.get("user");
                map.put("hasFollowed", hasFollowed(u.getId()));
            }
        }
        // userList中的每个map的键都有user、followTime、hasFollowed
        model.addAttribute("users", userList);

        return "/site/follower";
    }

    /**
     * 判断当前登陆用户对某个人的关注状态
     * @param userId
     * @return
     */
    private boolean hasFollowed(int userId) {
        if (hostHolder.getUser() == null) {
            return false;
        }

        return followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
    }

}
