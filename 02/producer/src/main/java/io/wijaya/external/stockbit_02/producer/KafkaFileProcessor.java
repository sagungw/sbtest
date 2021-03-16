package io.wijaya.external.stockbit_02.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KafkaFileProcessor {

    private KafkaProducer<String, String> kafkaProducer;

    private String kafkaTopic;

    private Pattern dataPattern;

    public KafkaFileProcessor(KafkaProducer<String, String> kafkaProducer, String topic) {
        this.kafkaProducer = kafkaProducer;
        this.kafkaTopic = topic;
        this.dataPattern = Pattern.compile("^\\d{4}-[a-zA-Z]+-\\d{2} \\d{2}:\\d{2}:\\d{2}\\|.+\\|\\d+$");
    }

    public void process(String file) throws Exception {
        String key = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        int lineCount = 0;

        BufferedReader bf = new BufferedReader(new FileReader(new File(file)));
        String line = bf.readLine();
        while (line != null) {
            Matcher m = this.dataPattern.matcher(line);
            if (!m.find()) {
                line = bf.readLine();
                continue;
            }

            ProducerRecord<String, String> r = new ProducerRecord<>(kafkaTopic, key, line);
            kafkaProducer.send(r);

            lineCount++;

            line = bf.readLine();
        }

        ProducerRecord<String, String> r = new ProducerRecord<>(this.kafkaTopic, key, String.format("len:%d", lineCount));
        kafkaProducer.send(r);

        kafkaProducer.flush();
    }

}
