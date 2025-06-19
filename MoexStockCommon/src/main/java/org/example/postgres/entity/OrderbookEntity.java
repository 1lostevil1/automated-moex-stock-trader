package org.example.postgres.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderbookEntity {
    private Long id;
    private String figi;
    private String instrumentUid;
    private int depth;
    private List<Order> bids;
    private List<Order> asks;
    private OffsetDateTime time;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Quotation {
        private long units;
        private int nano;

        public BigDecimal toBigDecimal() {
            return BigDecimal.valueOf(units).add(BigDecimal.valueOf(nano, 9));
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Order {
        private Quotation price;
        private long quantity;
    }
}