package org.example.message;

import org.example.postgres.entity.StockDataEntity;

import java.util.List;

public record ForecastRequest(String ticker, List<StockDataEntity> stocks)  {
}
