//package com.ncse.zhhygis.utils.baseUtils;
//
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.cache.annotation.EnableCaching;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.PropertySource;
//import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.core.StringRedisTemplate;
///**
// * ClassName:  [功能名称]
// * Description:  [一句话描述该类的功能]
// * Date:  2018/12/8 14 44
// *
// * @author Xugn
// * @version 1.0.0
// */
//
////PropertySource注解设置使用的resource目录下配置文件名称,
////不设置默认为resource/application.properties
////@PropertySource(value = "classpath:/redis.properties")
////航线信息redis
//@Configuration
//@EnableCaching
//public class ArticleRedisConfig extends RedisConfig {
//    @Value("${spring.redis2.database}")
//    private int dbIndex;
//    @Value("${spring.redis2.host}")
//    private String host;
//    @Value("${spring.redis2.port}")
//    private int port;
//    @Value("${spring.redis2.timeout}")
//    private int timeout;
//    @Value("${spring.redis2.password}")
//    private String password;
//
//    @Bean
//    public JedisConnectionFactory articleRedisConnectionFactory() {
//        return newJedisConnectionFactory(dbIndex,host,password, port, timeout);
//    }
//
//
//    @Bean(name = "ArticleRedisTemplate")
//    public RedisTemplate articleRedisTemplate() {
//        RedisTemplate template = new RedisTemplate();
//        template.setConnectionFactory(articleRedisConnectionFactory());
//        setSerializer(template); //设置序列化工具，这样ReportBean不需要实现Serializable接口
//        template.afterPropertiesSet();
//        return template;
//    }
//
//
//    @Bean(name = "ArticleStringRedisTemplate")
//    public StringRedisTemplate articleStringRedisTemplate() {
//        StringRedisTemplate template = new StringRedisTemplate();
//        template.setConnectionFactory(articleRedisConnectionFactory());
//        template.afterPropertiesSet();
//        return template;
//    }
//}
