package com.litiron.code.lineage.manager;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * @description: rocket消息管理器
 * @author: Litiron
 * @create: 2025-04-12 14:46
 **/
@Slf4j
public class RocketMQManager {

    private final RocketMQTemplate rocketMQTemplate;
    private final static String ROCKET_SQL_COLLECTOR_TOPIC = "sql-collector-topic";


    public void send(Object message) {
        try {
            rocketMQTemplate.convertAndSend(ROCKET_SQL_COLLECTOR_TOPIC, message);
        } catch (Exception e) {
            log.error("Failed to send rocket message", e);
        }
    }

    @Autowired
    public RocketMQManager(@Qualifier("sqlCollectorRocketMQTemplate") RocketMQTemplate rocketMQTemplate) {
        this.rocketMQTemplate = rocketMQTemplate;
    }
}
