package com.erebus.cactus.utils;


import cn.hutool.core.util.IdUtil;
import lombok.RequiredArgsConstructor;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@RequiredArgsConstructor
public class RocketSendUtil {
    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Value("${rocketmq.producer.topic}")
    private String topic;

    public SendStatus sendMessage(String tag, Object body) {
        String top = topic;
        String uuid = IdUtil.randomUUID();
        Message<Object> message = MessageBuilder.withPayload(body)
                .setHeader("KEYS", uuid).build();
        SendResult sendResult = rocketMQTemplate.syncSend(top + ":" + tag, message);
        return sendResult.getSendStatus();
    }
    public SendStatus sendMessage(String topic, String tag, Object body) {
        String uuid = IdUtil.randomUUID();
        Message<Object> message = MessageBuilder.withPayload(body)
                .setHeader("KEYS", uuid)
                .build();
        SendResult sendResult = rocketMQTemplate.syncSend(topic + ":" + tag, message);
        return sendResult.getSendStatus();
    }

}
