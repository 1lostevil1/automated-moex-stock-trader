package org.example.postgres.entity;

import java.util.List;

public class ForecastRequed{
    private String figi;
    private List<StockDataEntity> stocks;

    public ForecastRequed(String figi, List<StockDataEntity> stocks) {
        this.figi = figi;
        this.stocks = stocks;
    }

    public String getFigi() {
        return figi;
    }

    public List<StockDataEntity> getStocks() {
        return stocks;
    }

    public void setFigi(String figi) {
        this.figi = figi;
    }

    public void setStocks(List<StockDataEntity> stocks) {
        this.stocks = stocks;
    }
}
