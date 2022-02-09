package com.erin.community.controller;

import com.erin.community.entity.DiscussPost;
import com.erin.community.entity.Page;
import com.erin.community.entity.User;
import com.erin.community.service.DiscussPostService;
import com.erin.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: erin
 * \* To change this template use File | Settings | Editor | File and Code Templates | File | Class.
 * \* Description: 首页的controller
 * \
 */

@Controller
public class HomeController {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    /*
    * 方法调用之前，SpringMVC的dispatcherServlet会自动实例化Model和Page,并将Page注入Model
    * 所以在thymeleaf中可以直接访问Page对象中的数据
    *
    * @return 要展示的页面
    * */
    @RequestMapping(path = "/index", method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page) {
        // 获取所有的帖子数量，而不是指定某个用户（userId!=0），将其设置到Page对象中
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index");

        /*
        * 浏览器页面会给Page对象返回current(当前页面的页码)，limit(每页显示帖子的数量上限)
        * 并根据这两个变量得到当前页面帖子在discuss_post表中的的起始行
        * 但limit在这里是被page类写死了
        * 浏览器一开始访问/index后会在路径上拼接上current： /index?current=1
        * 如果点击分页部分的“下一页”，因为其<a>标签是这么写的：<a th:href="@{${page.path}(current=${page.current+1})}"></a>
        * 此时current已经被浏览器路径+1，浏览器就这样将current的值返回给page对象了，因此可以得到偏移量offset
        * */
        List<DiscussPost> list = discussPostService.findDiscussPosts(0, page.getOffset(), page.getLimit());
        // List<DiscussPost> list = discussPostService.findDiscussPosts(0, 0, 10);
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (list != null) {
            for (DiscussPost post : list) {
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);
                User user = userService.findUserById(post.getUserId());
                map.put("user", user);
                // map存储discuss_post实体类和user实体类
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts", discussPosts);
        // 返回模板的路径，默认是在templates目录下，后缀html可以省略
        return "/index";
    }
}
