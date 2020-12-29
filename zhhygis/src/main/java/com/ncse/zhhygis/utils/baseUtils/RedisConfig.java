package com.ncse.zhhygis.utils.baseUtils;

import org.jboss.logging.Logger;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.lang.reflect.Method;

/**
 * ClassName:  [功能名称]
 * Description:  [redis配置文件]
 * Date:  2018/12/4 13 47
 *
 * @author Xugn
 * @version 1.0.0
 */

@Configuration
@EnableCaching
public class RedisConfig {
//    @Value("${spring.redis.jedis.pool.max-idle}")
//    private int redisPoolMaxActive;
//    @Value("${spring.redis.jedis.pool.max-idle}")
//    private int redisPoolMaxIdle;
//    @Value("${spring.redis.jedis.pool.min-idle}")
//    private int redisPoolMinIdle;
//    @Value("${spring.redis.jedis.pool.max-wait}")
//    private int redisPoolMaxWait;
//    private final Logger log = Logger.getLogger(this.getClass());
//
//    @Bean
//    public KeyGenerator keyGenerator(){
//        return new KeyGenerator() {
//            @Override
//            public Object generate(Object target, Method method, Object... params) {
//                StringBuilder sb = new StringBuilder();
//                sb.append(target.getClass().getName());
//                sb.append(method.getName());
//                for (Object obj : params) {
//                    sb.append(obj.toString());
//                }
//                return sb.toString();
//            }
//        };
//    }
//
//    /**
//     * 创建连接
//     * @param host
//     * @param port
//     * @param timeout
//     * @return
//     */
//    public JedisConnectionFactory newJedisConnectionFactory(int index,String host,String password,int port,int timeout){
//        JedisConnectionFactory factory = new JedisConnectionFactory();
//        factory.setDatabase(index);
//        factory.setHostName(host);
//        factory.setPort(port);
//        factory.setPassword(password);
//        factory.setTimeout(timeout); //设置连接超时时间
//
//        //testOnBorrow为true时，返回的连接是经过测试可用的
//        factory.setPoolConfig(poolCofig(redisPoolMaxIdle,redisPoolMinIdle,redisPoolMaxActive,redisPoolMaxWait,true));
//
//        log.info("redis config========="+host);
//        return factory;
//    }
//
//    @Bean
//    public CacheManager cacheManager(RedisConnectionFactory connectionFactory){
//        CacheManager cacheManager =  RedisCacheManager.create(connectionFactory);
//        return cacheManager;
//    }
//
//    public JedisPoolConfig poolCofig(int maxIdle, int minIdle,int maxTotal,long maxWaitMillis,boolean testOnBorrow) {
//        JedisPoolConfig poolCofig = new JedisPoolConfig();
//        poolCofig.setMaxIdle(maxIdle);
//        poolCofig.setMinIdle(minIdle);
//        poolCofig.setMaxTotal(maxTotal);
//        poolCofig.setMaxWaitMillis(maxWaitMillis);
//        poolCofig.setTestOnBorrow(testOnBorrow);
//        return poolCofig;
//    }
//
//    public void setSerializer(RedisTemplate template) {
//        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
//        ObjectMapper om = new ObjectMapper();
//        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
//        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
//        jackson2JsonRedisSerializer.setObjectMapper(om);
//        template.setValueSerializer(jackson2JsonRedisSerializer);
//        template.setKeySerializer(new StringRedisSerializer());
//    }
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory){
        CacheManager cacheManager =  RedisCacheManager.create(connectionFactory);
        return cacheManager;
    }
    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory){
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<String,String>();
        redisTemplate.setConnectionFactory(factory);
        // key序列化方式;（不然会出现乱码;）,但是如果方法上有Long等非String类型的话，会报类型转换错误；
        // 所以在没有自己定义key生成策略的时候，以下这个代码建议不要这么写，可以不配置或者自己实现ObjectRedisSerializer
        // 或者JdkSerializationRedisSerializer序列化方式;
        RedisSerializer<String> redisSerializer = new StringRedisSerializer();// Long类型不可以会出现异常信息;
        redisTemplate.setKeySerializer(redisSerializer);
        redisTemplate.setHashKeySerializer(redisSerializer);
        redisTemplate.setValueSerializer(redisSerializer);
        redisTemplate.setHashValueSerializer(redisSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}
