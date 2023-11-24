package com.example.CameraControl.Service;

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

    private double cameraX;
    private double radarX;
    private double radarY;
    private double cameraY;
    private double targetDistance;
    private double targetBearing;

    @Value("${spring.kafka.producer.CameraLosStatus}")
    private String CameraLosStatusTopic;

    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;

    private static final Logger log = LoggerFactory.getLogger(ConsumeAndProduceService.class);

    @KafkaListener(topics = "${spring.kafka.consumer.tower}", groupId = "${spring.kafka.group-id}")
    public void consumeTower(@Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key, String value) {
        if(key.equals("camera")){
            cameraX = Double.parseDouble(value.split(",")[0]);
            cameraY = Double.parseDouble(value.split(",")[1]);
            log.info("camera Message: {} ", value);
        }
        if(key.equals("radar")) {
            radarX = Double.parseDouble(value.split(",")[0]);
            radarY = Double.parseDouble(value.split(",")[1]);
            log.info("radar Message: {} ", value);
        }
    }

    @KafkaListener(topics = "${spring.kafka.consumer.target}", groupId = "${spring.kafka.group-id}")
    public void consumeTarget(String value) {
        targetDistance = Double.parseDouble(value.split(",")[0]);
        targetBearing = Double.parseDouble(value.split(",")[1]);
        log.info("target Message: {} ", value);
        calculateCameraAngle();
    }
    public void calculateCameraAngle(){

        double targetPositionX = radarX+targetDistance*Math.cos(targetBearing);
        double targetPositionY = radarY+targetDistance*Math.sin(targetBearing);

        double angle = Math.atan2(targetPositionY - cameraY, targetPositionX - cameraX);

        kafkaTemplate.send(CameraLosStatusTopic, String.valueOf(angle));
        log.info("produced to topic: {}", angle);
        log.info("Degree of CameraAngle: {}", Math.toDegrees(angle));
    }
}
