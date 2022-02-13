package com.erin.community.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Properties;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: erin
 * \* To change this template use File | Settings | Editor | File and Code Templates | File | Class.
 * \* Description: Kaptcha的配置类
 * \
 */

@Configuration
public class KaptchaConfig {

    /*
    * 添加Bean注解之后，Spring容器就会管理这个bean
    * 主要是实例化DefaultKaptcha类实现的接口Producer
    * */
    @Bean
    public Producer kaptchaProducer() {
        // properties对象其实就是一个map，用于封装properties中的数据，但也可以直接实例化数据
        Properties properties = new Properties();
        properties.setProperty("kaptcha.image.width", "100"); // 单位默认是像素
        properties.setProperty("kaptcha.image.height", "40");
        properties.setProperty("kaptcha.textproducer.font.size", "32");
        properties.setProperty("kaptcha.textproducer.font.color", "0,0,0"); // 颜色设置为黑色
        properties.setProperty("kaptcha.textproducer.char.string", "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYAZ"); // 随机字符会在这个范围内取
        properties.setProperty("kaptcha.textproducer.char.length", "4");
        properties.setProperty("kaptcha.noise.impl", "com.google.code.kaptcha.impl.NoNoise"); // 不添加噪声

        DefaultKaptcha kaptcha = new DefaultKaptcha();
        Config config = new Config(properties);
        kaptcha.setConfig(config);
        return kaptcha;
    }

}

