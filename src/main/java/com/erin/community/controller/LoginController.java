package com.erin.community.controller;

import com.erin.community.entity.User;
import com.erin.community.service.UserService;
//import com.erin.community.util.CommunityConstant;
import com.erin.community.util.CommunityConstant;
import com.erin.community.util.CommunityUtil;
import com.erin.community.util.RedisKeyUtil;
import com.google.code.kaptcha.Producer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: erin
 * \* To change this template use File | Settings | Editor | File and Code Templates | File | Class.
 * \* Description: 管理和用户登陆相关的功能：注册、登陆、发送登陆需要的验证码
 * \
 */

@Controller
public class LoginController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private Producer kaptchaProducer;

    @Autowired
    private RedisTemplate redisTemplate;

    // 将application.properties文件中的值注入到该变量中，用于设置用户登陆后得到的cookie的作用域，是整个项目
    @Value("${server.servlet.context-path}")
    private String contextPath;

    @RequestMapping(path = "/register", method = RequestMethod.GET)
    public String getRegisterPage() {
        return "/site/register";
    }

    /**
    * spring mvc在调用该方法前会将user添加到model中
    * */
    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public String register(Model model, User user) {
        // user是浏览器传回给我们的，负责业务层的userService根据user中的属性来向数据库中插入user数据，并向user的邮箱发送html形式的激活邮件
        Map<String, Object> map = userService.register(user);
        // map如果为空，说明要插入的User数据不存在错误，注册用户成功，然后跳转到处理结果页面显示一些信息，然后再跳转到index即首页
        if (map == null || map.isEmpty()) {
            model.addAttribute("msg", "您已注册成功，我们已经向您的邮箱发送了一封激活邮件，请尽快激活!");
            model.addAttribute("target", "/index");
            return "/site/operate-result";
        } else { // 注册用户失败
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));
            return "/site/register";
        }
    }


    /**
    * @PathVariable将路径中的变量取出来,code是用户的激活码
    * 无论激活成功与否，都需要跳转到中间页面operate-result，然后在该页面再跳转到对应的页面
    *
    * */
    // http://localhost:8080/community/activation/101/code
    @RequestMapping(path = "/activation/{userId}/{code}", method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("userId") int userId, @PathVariable("code") String code) {
        int result = userService.activation(userId, code);
        if (result == ACTIVATION_SUCCESS) {
            model.addAttribute("msg", "激活成功,您的账号已经可以正常使用了!");
            model.addAttribute("target", "/login");
        } else if (result == ACTIVATION_REPEAT) {
            model.addAttribute("msg", "无效操作,该账号已经激活过了!");
            model.addAttribute("target", "/index");
        } else {
            model.addAttribute("msg", "激活失败,您提供的激活码不正确!");
            model.addAttribute("target", "/index");
        }
        return "/site/operate-result";
    }

    /**
     * 用户点击激活链接，服务端为用户激活之后跳转到登陆页面
     * */
    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String getLoginPage() {
        return "/site/login";
    }

    /**
    * 用户访问登陆页面的时候，验证码就需要显示，就需要访问/kaptcha路径，服务端生成验证码图片，响应客户端的时候发过去
    * 验证码是敏感信息，因此需要存到服务端，用session，但因为访问会很频繁，所以优化一下，将其存到Redis数据库中
    * */
    @RequestMapping(path = "/kaptcha", method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response/*, HttpSession session*/) {
        // 生成验证码，就是实现Producer接口的两个方法：createText和createImage
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

        // 将验证码存入session，便于之后登陆验证用
        // session.setAttribute("kaptcha", text);

        // 验证码的归属者，发送给客户端保存
        String kaptchaOwner = CommunityUtil.generateUUID();
        Cookie cookie = new Cookie("kaptchaOwner", kaptchaOwner);
        cookie.setMaxAge(60);
        cookie.setPath(contextPath);
        response.addCookie(cookie);
        // 将验证码存入Redis，便于之后登陆验证用
        String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
        redisTemplate.opsForValue().set(redisKey, text, 60, TimeUnit.SECONDS);

        // 将验证码图片输出给浏览器，声明服务端给客户端响应的是什么数据
        response.setContentType("image/png");
        try {
            // 获取response的字节流
            OutputStream os = response.getOutputStream();
            // 用os输出流输出，因为response是由spring mvc管理的，不用关闭
            ImageIO.write(image, "png", os);
        } catch (IOException e) {
            logger.error("响应验证码失败:" + e.getMessage());
        }
    }

    /**
    * 参数都是客户端传过来的，code是客户端提交表单传过来的验证码
    * response是为了向客户端传递登陆凭证，并让客户端存到cookie中，kaptchaOwner是从客户端传过来的cookie中得到的验证码归属者临时凭证
    * 如果有参数的类型不是基本类型，sping mvc会将其装到model中，客户端从而可以直接从model中得到该参数
    * 如果基本类型的参数没有在该方法中添加到model中，那么在页面中可以通过${param.参数名}的形式取出
    * */
    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String login(String username, String password, String code, boolean rememberme,
                        Model model/*, HttpSession session*/, HttpServletResponse response,
                        @CookieValue("kaptchaOwner") String kaptchaOwner) {
        // 检查验证码，如果验证码都不对就没必要继续判断用户名和密码了，getAttribute返回的是Object类型，因此需要强制类型转换
        // String kaptcha = (String) session.getAttribute("kaptcha");
        String kaptcha = null;
        // 验证码所属者的临时凭证不为空，表示存到Redis中的验证码没有失效
        // 因为存到cookie中的kaptchaOwner和数据以(kaptchaOwner,验证码)（分别对应key和value）的形式存到Redis中时都设置了失效时间
        // 如果kaptchaOwner为null，则Redis中以kaptchaOwner为key对应的value（验证码）也就查询不到
        if (StringUtils.isNotBlank(kaptchaOwner)) {
            String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
            kaptcha = (String) redisTemplate.opsForValue().get(redisKey);
        }

        if (StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)) {
            model.addAttribute("codeMsg", "验证码不正确!");
            return "/site/login";
        }

        // 检查账号,密码
        // 设置用户登陆有效的时长
        int expiredSeconds = rememberme ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;
        Map<String, Object> map = userService.login(username, password, expiredSeconds);
        // 通过map判断userService的login方法是否执行成功
        if (map.containsKey("ticket")) {
            // 登陆成功了就会将登陆凭证存到cookie中，并且通过response响应到客户端
            // map.get("ticket")获得的是一个对象，需要转换成字符串再添加到cookie中
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);
            // 登陆成功了就会重定向到首页
            return "redirect:/index";
        } else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "/site/login";
        }
    }

    @RequestMapping(path = "/logout", method = RequestMethod.GET)
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        // 重定向时默认访问的是get请求的login
        return "redirect:/login";
    }

}
