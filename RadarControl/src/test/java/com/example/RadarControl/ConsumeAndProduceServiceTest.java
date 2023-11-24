package com.example.RadarControl;

import com.example.RadarControl.service.ConsumeAndProduceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ConsumeAndProduceServiceTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private ConsumeAndProduceService consumeAndProduceService;

    @Test
    public void testConsumeTower() {
        // Arrange
        String key = "radar";
        String value = "10.0,20.0";

        // Act
        consumeAndProduceService.consumeTower(key, value);

        // Assert
        assertEquals(10.0, consumeAndProduceService.getTowerPositionX());
        assertEquals(20.0, consumeAndProduceService.getTowerPositionY());
    }

    @Test
    public void testConsumeTarget() {
        // Arrange
        String value = "30.0,40.0";

        // Act
        consumeAndProduceService.consumeTarget(value);

        // Assert
        assertEquals(30.0, consumeAndProduceService.getTargetPositionX());
        assertEquals(40.0, consumeAndProduceService.getTargetPositionY());
    }

    @Test
    public void testCalculatePolarCoordinate() {
        // Arrange
        consumeAndProduceService.setTowerPositionX(0.0);
        consumeAndProduceService.setTowerPositionY(0.0);
        consumeAndProduceService.setTargetPositionX(3.0);
        consumeAndProduceService.setTargetPositionY(4.0);
        consumeAndProduceService.setTargetBearingPositionTopic("TargetBearingPosition");
        // Act
        consumeAndProduceService.calculatePolarCoordinate();

        // Assert
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(kafkaTemplate).send(Mockito.eq("TargetBearingPosition"), messageCaptor.capture());
        String producedMessage = messageCaptor.getValue();

        double expectedDistance = 5.0;
        double expectedAngle = 0.9272952180016122;
        String expectedMessage = expectedDistance+","+expectedAngle;

        assertEquals(expectedMessage,producedMessage);
    }
}
