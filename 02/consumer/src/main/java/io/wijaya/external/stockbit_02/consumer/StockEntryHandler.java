package io.wijaya.external.stockbit_02.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StockEntryHandler {

    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    private SynchronizedFileWriter writer;

    public StockEntryHandler(SynchronizedFileWriter writer) {
        this.writer = writer;
    }

    public void handle(List<StockEntry> entries) {
        Map<String, List<StockEntry>> groupedEntries = entries.parallelStream()
                .collect(Collectors.groupingBy(entry -> String.format("%s-%s",
                        entry.getTimestamp().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")),
                        entry.getCode()
                    )
                ));

        List<StockEntrySummary> summaries = groupedEntries.entrySet().parallelStream().map(entrySet -> {
            Integer max = entrySet.getValue().parallelStream().mapToInt(StockEntry::getPrice).max().orElse(0);
            Integer min = entrySet.getValue().parallelStream().mapToInt(StockEntry::getPrice).min().orElse(0);

            String formattedTime = entrySet.getValue().get(0).getTimestamp().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            String line = String.format("%s|%s|high;%s|low;%s", formattedTime, entrySet.getValue().get(0).getCode(), max, min);
            return new StockEntrySummary(formattedTime, line);
        }).sorted(Comparator.comparing(StockEntrySummary::getFormattedTime)).collect(Collectors.toList());

        summaries.forEach(s -> {
            try {
                writer.writeLine(s.getVal());
            } catch (Exception e) {
                logger.error(e.toString());
            }
        });

        logger.info("output file written");
    }

}
