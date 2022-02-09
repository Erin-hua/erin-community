package com.erin.community;

import com.erin.community.CommunityApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: erin
 * \* To change this template use File | Settings | Editor | File and Code Templates | File | Class.
 * \* Description: 测试logger
 * \
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class LoggerTests {

    private static final Logger logger = LoggerFactory.getLogger(LoggerTests.class);

    @Test
    public void testLogger() {
        // 打印名字
        System.out.println(logger.getName());

        // logger.trace();是最低级别，下面的越往下级别越高
        // 打印完信息后还需要在配置中声明logger需要打印的级别，在application.properties文件中配置级别
        logger.debug("debug log");
        logger.info("info log");
        logger.warn("warn log");
        logger.error("error log");
    }

}

