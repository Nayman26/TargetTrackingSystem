package com.example.RadarControl.service;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Service
@Getter
@Setter
public class ConsumeAndProduceService {

    private double towerPositionX;
    private double towerPositionY;
    private double targetPositionX;
    private double targetPositionY;

    @Value("${spring.kafka.producer.targetBearing}")
    private String TargetBearingPositionTopic;

    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;

    private static final Logger log = LoggerFactory.getLogger(ConsumeAndProduceService.class);

    @KafkaListener(topics = "${spring.kafka.consumer.tower}", groupId = "${spring.kafka.group-id}")
    public void consumeTower(@Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key, String value) {
        if(key.equals("radar")) {
            towerPositionX = Double.parseDouble(value.split(",")[0]);
            towerPositionY = Double.parseDouble(value.split(",")[1]);
            log.info("tower Message: {} ", value);
        }
    }

    @KafkaListener(topics = "${spring.kafka.consumer.target}", groupId = "${spring.kafka.group-id}")
    public void consumeTarget(String value) {
        targetPositionX = Double.parseDouble(value.split(",")[0]);
        targetPositionY = Double.parseDouble(value.split(",")[1]);
        log.info("target Message: {} ", value);
        calculatePolarCoordinate();
    }

    public void calculatePolarCoordinate(){
        double distance = Math.sqrt((Math.pow(towerPositionX-targetPositionX, 2) + Math.pow(towerPositionX-targetPositionY, 2)));
        double angle = Math.atan2(targetPositionY-towerPositionY,targetPositionX-towerPositionX);
        String message = distance+","+angle;
        kafkaTemplate.send(TargetBearingPositionTopic,message);
        log.info("produced to topic: {} ", message);
    }
}
