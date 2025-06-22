package org.example.postgres.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockDataEntity {
    private Long id;
    private String figi;
    private String instrumentUid;
    private String ticker;
    private OffsetDateTime time;
    private BigDecimal openPrice;
    private BigDecimal closePrice;
    private BigDecimal highPrice;
    private BigDecimal lowPrice;
    private Long volume;
    private Long bidVolume;
    private Long askVolume;
    private Long buyVolume;
    private Long sellVolume;
    private BigDecimal rsi;
    private BigDecimal macd;
    private BigDecimal ema;
}
