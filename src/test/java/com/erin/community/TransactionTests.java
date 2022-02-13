package com.erin.community;

import com.erin.community.service.ErinService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: erin
 * \* To change this template use File | Settings | Editor | File and Code Templates | File | Class.
 * \* Description: 测试事务
 * \
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class TransactionTests {

    @Autowired
    private ErinService erinService;

    @Test
    public void testSave1() {
        Object obj = erinService.save1();
        System.out.println(obj);
    }

    @Test
    public void testSave2() {
        Object obj = erinService.save2();
        System.out.println(obj);
    }

}
