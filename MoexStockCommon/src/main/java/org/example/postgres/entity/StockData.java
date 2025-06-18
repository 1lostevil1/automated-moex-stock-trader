package org.example.postgres.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockData {
    private Long id;
    private String ticker;
    private LocalDateTime ts_from;
    private LocalDateTime ts_to;
    private BigDecimal openPrice;
    private BigDecimal closePrice;
    private BigDecimal highPrice;
    private BigDecimal lowPrice;
    private Long volume;
    private Long buyVolume;
    private Long sellVolume;
    private BigDecimal rsi;
    private BigDecimal macd;
    private BigDecimal ema;
    private LocalDateTime createdAt;
}
