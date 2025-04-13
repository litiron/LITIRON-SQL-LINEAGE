package com.litiron.code.lineage.config;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;

/**
 * @description: 自定义rocket mq配置
 * @author: Litiron
 * @create: 2025-04-12 15:49
 **/
public class RocketMQCollectorConfig {

    private final static String ROCKET_SERVE_ADDR = "120.26.221.190:9876";
    private final static String ROCKET_SQL_COLLECTOR_GROUP = "sql-collector-group";


    @Bean("sqlCollectorRocketMQTemplate")
    public RocketMQTemplate customRocketTemplate(@Qualifier("enhanceRocketMQMessageConverter")RocketMQMessageConverter rocketMQMessageConverter,
                                                 @Qualifier("sqlCollectorMQProducer") DefaultMQProducer producer) {
        RocketMQTemplate template = new RocketMQTemplate();
        template.setMessageConverter(rocketMQMessageConverter.getMessageConverter());
        template.setProducer(producer);
        return template;
    }

    @Bean("sqlCollectorMQProducer")
    public DefaultMQProducer customProducer() {
        // 初始化 Producer
        DefaultMQProducer producer = new DefaultMQProducer(ROCKET_SQL_COLLECTOR_GROUP);
        producer.setNamesrvAddr(ROCKET_SERVE_ADDR);
        producer.setProducerGroup(ROCKET_SQL_COLLECTOR_GROUP);
        // 消息体超过 1024 字节时自动压缩
        producer.setCompressMsgBodyOverHowmuch(1024);
        // 存储失败时尝试其他 Broker
        producer.setRetryAnotherBrokerWhenNotStoreOK(true);
        return producer;
    }
}
