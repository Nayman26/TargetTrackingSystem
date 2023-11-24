package com.example.CameraControl;

import com.example.CameraControl.Service.ConsumeAndProduceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsumeAndProduceServiceTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private ConsumeAndProduceService consumeAndProduceService;

    @Test
    void testConsumeTowerCamera() {
        consumeAndProduceService.consumeTower("camera", "10.5,15.7");

        assertEquals(10.5, consumeAndProduceService.getCameraX());
        assertEquals(15.7, consumeAndProduceService.getCameraY());

        verify(kafkaTemplate, never()).send(any(), any());
    }

    @Test
    void testConsumeTowerRadar() {
        consumeAndProduceService.consumeTower("radar", "20.3,30.8");

        assertEquals(20.3, consumeAndProduceService.getRadarX());
        assertEquals(30.8, consumeAndProduceService.getRadarY());

        verify(kafkaTemplate, never()).send(any(), any());
    }

    @Test
    void testConsumeTarget() {
        consumeAndProduceService.consumeTarget("100.0,1.5");

        assertEquals(100.0, consumeAndProduceService.getTargetDistance());
        assertEquals(1.5, consumeAndProduceService.getTargetBearing());

        verify(kafkaTemplate).send(any(), any());
    }

    @Test
    void testCalculateCameraAngle() {
        // Set up the required state in the consumeAndProduceService instance
        consumeAndProduceService.setRadarX(10.0);
        consumeAndProduceService.setRadarY(20.0);
        consumeAndProduceService.setCameraX(5.0);
        consumeAndProduceService.setCameraY(10.0);
        consumeAndProduceService.setTargetDistance(50.0);
        consumeAndProduceService.setTargetBearing(Math.toRadians(45.0));
        consumeAndProduceService.setCameraLosStatusTopic("ConsoleLosStatus");
        consumeAndProduceService.calculateCameraAngle();

        // Add assertions or verifications based on your business logic
        // For example, you can verify that the kafkaTemplate.send() method is called with the expected arguments.
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(kafkaTemplate).send(Mockito.eq("ConsoleLosStatus"), messageCaptor.capture());

        String producedMessage = messageCaptor.getValue();
        String expectedCameraAngle = "0.8436679133128061";
        assertEquals(expectedCameraAngle,producedMessage);
    }

}