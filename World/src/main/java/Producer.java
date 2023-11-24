import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Producer {
    private static final Logger log = LoggerFactory.getLogger(Producer.class);

    public void produceTarget(String TargetPointPosition) {
        log.info("Target Producer");
        Properties properties = setProperties();

        KafkaProducer<String, String> targetProducer = new KafkaProducer<>(properties);
        targetProducer.send(new ProducerRecord<>("TargetPointPosition", TargetPointPosition));
        targetProducer.flush();
        targetProducer.close();
    }

    public void produceTower(HashMap<String, String> towerPositions) {
        log.info("Tower Producer");
        Properties properties = setProperties();
        KafkaProducer<String, String> towerProducer = new KafkaProducer<>(properties);
        for (Map.Entry<String, String> entry : towerPositions.entrySet()) {
            towerProducer.send(new ProducerRecord<>("TowerPosition", entry.getKey(), entry.getValue()));
            towerProducer.flush();
        }
        towerProducer.close();
    }

    private Properties setProperties(){
        String bootstrapServers = "127.0.0.1:9092";

        // create Producer properties
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        return properties;
    }
}