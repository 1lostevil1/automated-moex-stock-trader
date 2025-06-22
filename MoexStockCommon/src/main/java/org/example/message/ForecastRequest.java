package org.example.message;

import org.example.postgres.entity.StockDataEntity;

import java.util.List;

public record ForecastRequest(String figi, List<StockDataEntity> stocks)  {
}
