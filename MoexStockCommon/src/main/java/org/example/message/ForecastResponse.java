package org.example.message;

import java.time.OffsetDateTime;

public record ForecastResponse(String ticker, double closePrice,double lastPrice, OffsetDateTime timing) {
}
