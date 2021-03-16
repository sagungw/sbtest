package io.wijaya.external.stockbit_02.producer;

import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class App {

    public static void main(String[] args) {
        if (args.length == 0) {
            return;
        }

        Logger logger = LoggerFactory.getLogger(App.class.getName());

        String kafkaAddress = StringUtils.defaultIfEmpty(System.getenv("KAFKA_ADDRESS"), "localhost:9092");
        String kafkaTopic = StringUtils.defaultIfEmpty(System.getenv("KAFKA_TOPIC"), "stockbit");
        String output = StringUtils.defaultIfEmpty(System.getenv("INPUT"), "/Users/sagungwijaya/test3.txt");

        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaAddress);
        properties.setProperty(ProducerConfig.ACKS_CONFIG, "1");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<String, String>(properties);

        KafkaFileProcessor fileProcessor = new KafkaFileProcessor(kafkaProducer, kafkaTopic);
        try {
            fileProcessor.process(args[0]);
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

}
