package edu.sjsu.android.stocksearchapp;

public class HistoryItem {
    private String date;
    private String open;
    private String close;
    private String high;
    private String low;
    private String volume;

    public HistoryItem(String date, String open, String close,
                       String high, String low, String volume) {
        this.date = date;
        this.open = open;
        this.close = close;
        this.high = high;
        this.low = low;
        this.volume = volume;
    }

    public String getDate() {
        return date;
    }

    public String getOpen() {
        return open;
    }

    public String getClose() {
        return close;
    }

    public String getHigh() {
        return high;
    }

    public String getLow() {
        return low;
    }

    public String getVolume() {
        return volume;
    }
}
