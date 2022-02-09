package com.erin.community;

import com.erin.community.dao.ErinDao;
import com.erin.community.dao.ErinDaoImpl;
import com.erin.community.service.ErinService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.SimpleFormatter;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
class CommunityApplicationTests implements ApplicationContextAware {

	private ApplicationContext applicationContext;

	// ApplicationContext就是容器
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Test
	public void testApplicationContext() {
		System.out.println(applicationContext);
		ErinDao erindao = applicationContext.getBean(ErinDao.class);
		System.out.println(erindao.select());

		ErinDao erinDao = applicationContext.getBean("erinDaoImpl", ErinDao.class);
		System.out.println(erinDao.select());
	}

	@Test
	public void testBeanManagement() {
		ErinService erinService = applicationContext.getBean(ErinService.class);
		System.out.println(erinService);

		erinService = applicationContext.getBean(ErinService.class);
		System.out.println(erinService);
	}

	@Test
	public void testBeanConfig() {
		SimpleDateFormat simpleDateFormat = applicationContext.getBean(SimpleDateFormat.class);
		System.out.println(simpleDateFormat.format(new Date()));
	}

	// 表示spring容器将ErinDao对象注入到该属性中，此处默认的是ErinDaoMybatisImpl
	@Autowired
	// 注意ErinDaoImpl的bean id已经被Repository注解赋值为了erinDaoIml
	@Qualifier("erinDaoImpl")
	private ErinDao erinDao;

	@Autowired
	private ErinService erinService;

	@Autowired
	private SimpleDateFormat simpleDateFormat;

	@Test
	public void testDI() {
		System.out.println(erinDao);
		System.out.println(erinService);
		System.out.println(simpleDateFormat);
	}
}

