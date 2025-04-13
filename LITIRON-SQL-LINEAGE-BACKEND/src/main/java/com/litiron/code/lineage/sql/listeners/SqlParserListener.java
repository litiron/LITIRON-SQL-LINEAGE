package com.litiron.code.lineage.sql.listeners;

import cn.hutool.json.JSONUtil;
import com.litiron.code.lineage.dto.SqlExecuteInfoDto;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * @description: SQL解析监听器
 * @author: Litiron
 * @create: 2025-04-13 14:53
 **/
@Component
@RocketMQMessageListener(nameServer = "${rocketmq.name-server}", topic = "${rocketmq.consumer.topic}", consumerGroup = "${rocketmq.consumer.group}")
public class SqlParserListener implements RocketMQListener<Object> {


    @Override
    public void onMessage(Object message) {
        System.out.println(JSONUtil.toJsonStr(message));
    }
}
