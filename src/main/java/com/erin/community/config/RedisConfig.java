package com.erin.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: erin
 * \* To change this template use File | Settings | Editor | File and Code Templates | File | Class.
 * \* Description: Redis配置类
 * \
 */

@Configuration
public class RedisConfig {

    /**
     * 定义第三方bean，将哪个对象装配到容器中就返回哪个类型的对象
     * 方法名是bean的名字
     * @param factory 连接工厂，spring容器会自动将这个参数注入给bean redisTemplate
     * @return
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory); // 具备访问数据库的能力

        // 设置key的序列化方式（将java的数据存到Redis数据库中需要指定序列化方式）
        template.setKeySerializer(RedisSerializer.string());
        // 设置普通value的序列化方式
        template.setValueSerializer(RedisSerializer.json());
        // 设置hash的key的序列化方式
        template.setHashKeySerializer(RedisSerializer.string());
        // 设置hash的value的序列化方式
        template.setHashValueSerializer(RedisSerializer.json());

        // 触发上面的设置使其生效
        template.afterPropertiesSet();
        return template;
    }

}
