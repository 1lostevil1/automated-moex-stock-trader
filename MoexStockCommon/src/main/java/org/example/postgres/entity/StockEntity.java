package org.example.postgres.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockEntity {
    private String figi;
    private String instrumentUid;
    private String ticker;
    private String name;
}
