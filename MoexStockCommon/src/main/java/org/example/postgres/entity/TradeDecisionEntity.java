package org.example.postgres.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TradeDecisionEntity {
    private Long id;
    private String ticker;
    private BigDecimal price;
    private BigDecimal lastPrice;
    private BigDecimal stopLoss;
    private BigDecimal takeProfit;
    private TradeDecisionDirection direction;
    private OffsetDateTime createdAt;
}
