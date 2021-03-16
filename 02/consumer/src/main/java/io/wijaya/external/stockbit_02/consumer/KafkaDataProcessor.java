package io.wijaya.external.stockbit_02.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class KafkaDataProcessor {

    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    private KafkaConsumer<String, String> kafkaConsumer;

    private StockEntryHandler stockEntryHandler;

    private Map<String, List<StockEntry>> stockEntries;

    private Map<String, Integer> stockEntryLengths;

    public KafkaDataProcessor(KafkaConsumer<String, String> kafkaConsumer, String kafkaTopic, StockEntryHandler entryHandler) {
        this.kafkaConsumer = kafkaConsumer;
        this.kafkaConsumer.subscribe(Collections.singleton(kafkaTopic));
        this.stockEntryHandler = entryHandler;
        this.stockEntries = new HashMap<>();
        this.stockEntryLengths = new HashMap<>();
    }

    public synchronized void insertStockEntry(String key, StockEntry entry) {
        List<StockEntry> entries = stockEntries.get(key);
        if (entries == null)
            entries = new ArrayList<>();

        entries.add(entry);
        stockEntries.put(key, entries);
    }

    public synchronized void insertStockEntryLength(String key, int len) {
        stockEntryLengths.put(key, len);
    }

    public synchronized List<List<StockEntry>> getStockEntriesCopy() {
        List<List<StockEntry>> r = new ArrayList<>();

        for (Map.Entry<String, List<StockEntry>> entry: stockEntries.entrySet()) {
            Integer expectedLen = stockEntryLengths.get(entry.getKey());
            if (expectedLen == null)
                continue;

            if (expectedLen != entry.getValue().size())
                continue;

            r.add(Collections.unmodifiableList(entry.getValue()));
            stockEntryLengths.put(entry.getKey(), null);
        }

        return r;
    }

    public void process() {
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        while(true) {
            for (List<StockEntry> copy: getStockEntriesCopy()) {
                executor.submit(() -> {
                    stockEntryHandler.handle(copy);
                });
            }

            ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, String> record: records) {
                try {
                    executor.submit(() -> {
                        try {
                            if (record.value().startsWith("len:")) {
                                String len = record.value().split(":")[1];
                                insertStockEntryLength(record.key(), Integer.parseInt(len));
                                return;
                            }

                            insertStockEntry(record.key(), StockEntry.parse(record.value()));
                        } catch (Exception e) {
                            logger.error(e.toString());
                        }
                    });
                } catch (Exception e) {
                    logger.error(e.toString());
                }
            }
        }

    }

}
