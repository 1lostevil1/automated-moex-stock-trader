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
public class TradeEntity {
    private Long id;
    private String figi;
    private String instrumentUid;
    private TradeDirection direction;
    private BigDecimal price;
    private Long quantity;
    private OffsetDateTime time;

    public enum TradeDirection {
        BUY,
        SELL
    }
}

