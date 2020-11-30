package edu.sjsu.android.stocksearchapp;

public class FavoriteItem {
    private String currentJSONFav;
    private String historyJSONFav;
    private String tickerFav;
    private double lastPriceFav;
    private double percentageFav;

    public FavoriteItem(String currentJSONFav, String historyJSONFav, String tickerFav,
                        double lastPriceFav, double percentageFav) {
        this.currentJSONFav = currentJSONFav;
        this.historyJSONFav = historyJSONFav;
        this.tickerFav = tickerFav;
        this.lastPriceFav = lastPriceFav;
        this.percentageFav = percentageFav;
    }

    public String getCurrentJSONFav() {
        return currentJSONFav;
    }

    public String getHistoryJSONFav() {
        return historyJSONFav;
    }

    public String getTickerFav() {
        return tickerFav;
    }

    public double getLastPriceFav() {
        return lastPriceFav;
    }

    public double getPercentageFav() {
        return percentageFav;
    }
}
