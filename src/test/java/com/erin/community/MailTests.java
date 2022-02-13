package com.erin.community;

import com.erin.community.util.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: erin
 * \* To change this template use File | Settings | Editor | File and Code Templates | File | Class.
 * \* Description: 测试发送邮件的功能
 * \
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTests {

    @Autowired
    private MailClient mailClient;

    // Thymeleaf模板引擎的核心类已经被Spring管理了，直接注入，负责生成动态网页，而不是发送网页
    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void testTextMail() {
        mailClient.sendMail("erin_hua939@sina.com", "TEST", "Welcome.");
    }

    /*
    * 发送HTML邮件
    *
    * */
    @Test
    public void testHtmlMail() {
        // 把要传给模板的变量存到这个对象中即可
        Context context = new Context();
        context.setVariable("username", "Erin Sakura");

        // 调用模板引擎生成动态网页(其实是字符串)
        String content = templateEngine.process("/mail/demo", context);
        System.out.println(content);

        mailClient.sendMail("erin_hua939@sina.com", "HTML", content);
    }

}
