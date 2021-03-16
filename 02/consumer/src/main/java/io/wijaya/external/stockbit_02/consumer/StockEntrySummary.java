package io.wijaya.external.stockbit_02.consumer;

public class StockEntrySummary {

    private String formattedTime;

    private String val;

    public StockEntrySummary(String f, String v) {
        this.formattedTime = f;
        this.val = v;
    }

    public String getFormattedTime() {
        return formattedTime;
    }

    public String getVal() {
        return val;
    }
}
