package com.erin.community.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: erin
 * \* To change this template use File | Settings | Editor | File and Code Templates | File | Class.
 * \* Description: 把发邮件委托给qq邮箱，代替邮箱的客户端
 * \
 */

@Component
public class MailClient {

    // 需要生成日志
    private static final Logger logger = LoggerFactory.getLogger(MailClient.class);

    // 是Spring Email的核心组件
    @Autowired
    private JavaMailSender mailSender;

    // 指定发送邮件的是application.properties文件中配置好了的邮箱
    @Value("${spring.mail.username}")
    private String from;

    public void sendMail(String to, String subject, String content) {
        try {
            // 邮件模板，封装邮件的相关信息
            MimeMessage message = mailSender.createMimeMessage();
            // 帮助构建message中的内容
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            // 第二个参数表示支持传入HTML，而不仅仅只是简单的文本
            helper.setText(content, true);
            // 发送邮件
            mailSender.send(helper.getMimeMessage());
        } catch (MessagingException e) {
            logger.error("发送邮件失败:" + e.getMessage());
        }
    }

}
