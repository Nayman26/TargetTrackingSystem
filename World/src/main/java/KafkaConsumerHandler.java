import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class KafkaConsumerHandler {

    private Consumer<String, String> consumer;

    public KafkaConsumerHandler(String bootstrapServers, String groupId, String topic) {
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("group.id", groupId);
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());

        this.consumer = new KafkaConsumer<>(props);
        this.consumer.subscribe(Collections.singletonList(topic));
    }

    public void close() {
        consumer.close();
    }

    public void consumeKafkaValues(AnimatedPanel animatedPanel) {
        // Poll for new messages
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
        System.out.println(records);

        for (ConsumerRecord<String, String> record : records) {
            // Parse Kafka message and update xPos and yPos
            try {
                animatedPanel.setCameraAngle(Double.parseDouble(record.value()));
                System.out.println("--------------------------");
                System.out.println("cameraAngle: "+record.value());

            } catch (NumberFormatException e) {
                // Handle parsing errors
                System.err.println("Error parsing Kafka message: " + record.value());
                e.printStackTrace();
            }
        }
    }
}