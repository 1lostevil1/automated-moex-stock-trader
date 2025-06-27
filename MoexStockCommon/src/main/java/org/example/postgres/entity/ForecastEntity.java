package org.example.postgres.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForecastEntity {
    private Long id;
    private String ticker;
    private BigDecimal closePrice;
    private BigDecimal lastPrice;
    private OffsetDateTime timing;
}
