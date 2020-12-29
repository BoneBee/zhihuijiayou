package com.ncse.zhhygis.collect;


import com.alibaba.fastjson.JSON;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
public class KafkaProServer {

    private final Logger log = Logger.getLogger(this.getClass());

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Value("${kafka.producer.topicName}")
    private String topicName;
    @Value("${kafka.producer.key}")
    private String key;

    public void sendKafka(Object obj) {
        try {
            log.info("kafka发送的消息=" + obj);
            String objs = JSON.toJSONString(obj);
            kafkaTemplate.send(topicName, key, objs);
            log.info("发送kafka成功.");
        } catch (Exception e) {
            log.info("发送kafka失败" + e);
        }
    }

}