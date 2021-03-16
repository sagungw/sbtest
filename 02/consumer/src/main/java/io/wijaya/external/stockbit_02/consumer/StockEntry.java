package io.wijaya.external.stockbit_02.consumer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StockEntry {

    private LocalDateTime timestamp;

    private String code;

    private Integer price;

    private StockEntry(){}

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getCode() {
        return code;
    }

    public Integer getPrice() {
        return price;
    }

    public static StockEntry parse(String s) throws Exception {
        String[] parts = s.split("\\|");
        if (parts.length < 3) {
            throw new IllegalArgumentException("invalid entry");
        }

        StockEntry entry = new StockEntry();
        entry.timestamp = LocalDateTime.parse(parts[0], DateTimeFormatter.ofPattern("yyyy-MMM-dd HH:mm:ss")).withSecond(0);
        entry.code = parts[1];
        entry.price = Integer.parseInt(parts[2]);

        return entry;
    }
}
