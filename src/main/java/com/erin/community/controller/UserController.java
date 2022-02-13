package com.erin.community.controller;

import com.erin.community.annotation.LoginRequired;
import com.erin.community.entity.User;
import com.erin.community.service.UserService;
import com.erin.community.util.CommunityUtil;
import com.erin.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: erin
 * \* To change this template use File | Settings | Editor | File and Code Templates | File | Class.
 * \* Description: 用户帐号设置模块的controller
 * \
 */

@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    // 用户给服务端上传文件后服务器端存放文件的路径
    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    // 项目的访问路径
    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    // 需要更新当前用户的头像，就需要从当前线程对象中获得user对象
    @Autowired
    private HostHolder hostHolder;

    /*
    * 访问帐号设置页面
    * */
    @LoginRequired
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage() {
        return "/site/setting";
    }

    /*
    * 上传头像
    * */
    @LoginRequired
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model) {
        if (headerImage == null) {
            model.addAttribute("error", "您还没有选择图片!");
            return "/site/setting";
        }

        // 获得原始文件名
        String fileName = headerImage.getOriginalFilename();
        // 获取文件的后缀名
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        // 如果后缀名为空，则当前传的文件格式有误
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "文件的格式不正确!");
            return "/site/setting";
        }

        // 生成随机文件名
        fileName = CommunityUtil.generateUUID() + suffix;
        // 确定客户端上传的文件在服务器端存放的路径
        File dest = new File(uploadPath + "/" + fileName);
        try {
            // 存储文件
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败: " + e.getMessage());
            throw new RuntimeException("上传文件失败,服务器发生异常!", e);
        }

        // 更新当前用户的头像的路径(web访问路径)
        // http://localhost:8080/community/user/header/xxx.png
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateHeader(user.getId(), headerUrl);

        // 更新成功就重定向到index
        return "redirect:/index";
    }

    /*
    * 通过web路径获取用户的头像
    * 因为方法向浏览器响应的是二进制的图片数据，因此需要通过流手动向浏览器输出（通过response）
    * 用户不登陆也可以点击首页帖子的用户并获取其头像
    *
    * */
    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        // uploadPath是用户给服务端上传文件后服务器端存放文件的路径
        fileName = uploadPath + "/" + fileName;
        // 文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        // 响应图片
        response.setContentType("image/" + suffix);
        try (
                // java7的写法，在括号中的变量最后在编译的时候会自动在catch后加上finally，关闭输入流，前提是fis有close方法
                FileInputStream fis = new FileInputStream(fileName);
                OutputStream os = response.getOutputStream();
        ) {
            // 建立缓冲区，增加效率
            byte[] buffer = new byte[1024];
            int b = 0;
            // fis从文件的数据流中读取数据并存到buffer数组中，b等于-1说明没读到数据，b的范围是0-1023
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败: " + e.getMessage());
        }
    }

}

