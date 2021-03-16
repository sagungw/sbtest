package io.wijaya.external.stockbit_02.consumer;

import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Properties;

public class App {

    public static void main(String[] args) {
        String kafkaAddress = StringUtils.defaultIfEmpty(System.getenv("KAFKA_ADDRESS"), "localhost:9092");
        String kafkaTopic = StringUtils.defaultIfEmpty(System.getenv("KAFKA_TOPIC"), "stockbit");
        String output = StringUtils.defaultIfEmpty(System.getenv("OUTPUT"), "/Users/sagungwijaya/output.txt");

        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaAddress);
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "stock-consumers");
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(properties);

        SynchronizedFileWriter writer = new SynchronizedFileWriter(output);

        StockEntryHandler entryHandler = new StockEntryHandler(writer);

        KafkaDataProcessor kafkaDataProcessor = new KafkaDataProcessor(kafkaConsumer, kafkaTopic, entryHandler);
        kafkaDataProcessor.process();

        kafkaConsumer.close();
    }

}
