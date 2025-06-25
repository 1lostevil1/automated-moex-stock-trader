package org.example.postgres.entity;


import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("instrument_uid")
    private String instrumentUid;
    private String ticker;
    private OffsetDateTime time;
    @JsonProperty("open_price")
    private BigDecimal openPrice;
    @JsonProperty("close_price")
    private BigDecimal closePrice;
    @JsonProperty("high_price")
    private BigDecimal highPrice;
    @JsonProperty("low_price")
    private BigDecimal lowPrice;
    private Long volume;
    @JsonProperty("bid_volume")
    private Long bidVolume;
    @JsonProperty("ask_volume")
    private Long askVolume;
    @JsonProperty("buy_volume")
    private Long buyVolume;
    @JsonProperty("sell_volume")
    private Long sellVolume;
    private BigDecimal rsi;
    private BigDecimal macd;
    private BigDecimal ema;
}
