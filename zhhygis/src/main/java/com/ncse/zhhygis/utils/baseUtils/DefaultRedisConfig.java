//package com.ncse.zhhygis.utils.baseUtils;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.cache.annotation.EnableCaching;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.context.annotation.PropertySource;
//import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.core.StringRedisTemplate;
//
////PropertySource注解设置使用的resource目录下配置文件名称,
////不设置默认为resource/application.properties
////@PropertySource(value = "classpath:/redis.properties")
////车辆信息redis
//@Configuration
//@EnableCaching
//public class DefaultRedisConfig extends RedisConfig {
//
//    @Value("${spring.redis.database}")
//    private int dbIndex;
//    @Value("${spring.redis.host}")
//    private String host;
//    @Value("${spring.redis.password}")
//    private String password;
//    @Value("${spring.redis.port}")
//    private int port;
//    @Value("${spring.redis.timeout}")
//    private int timeout;
//
//    @Primary
//    @Bean
//    public JedisConnectionFactory defaultRedisConnectionFactory() {
//        return newJedisConnectionFactory(dbIndex,host,password, port, timeout);
//    }
//
//    @Bean(name = "DefaultRedisTemplate")
//    public RedisTemplate defaultRedisTemplate() {
//        RedisTemplate template = new RedisTemplate();
//        template.setConnectionFactory(defaultRedisConnectionFactory());
//        setSerializer(template);
//        template.afterPropertiesSet();
//        return template;
//    }
//
//    @Bean(name = "DefaultStringRedisTemplate")
//    public StringRedisTemplate defaultStringRedisTemplate() {
//        StringRedisTemplate template = new StringRedisTemplate();
//        template.setConnectionFactory(defaultRedisConnectionFactory());
//        template.afterPropertiesSet();
//        return template;
//    }
//}