package com.devcodes.training.itscreen;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TaskReport {


    @Value("${kafka.groupid}")
    private String groupId;

    @Scheduled(fixedRateString = "${schedules.report}")
    public void doSendReport(){
        sendMessage("screen:"+groupId);
    }

    public void sendMessage(String msg) {
        kafkaTemplate.send(topicName, msg);
    }

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value("${kafka.topic.dst}")
    private String topicName;
}
