package org.example.util.dtoMapper;

import org.example.postgres.entity.TradeEntity;
import ru.tinkoff.piapi.contract.v1.Trade;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class TradeMapper {

    public static TradeEntity mapApiTradeToEntity(Trade apiTrade) {
        return TradeEntity.builder()
                .figi(apiTrade.getFigi())
                .instrumentUid(apiTrade.getInstrumentUid())
                .time(OffsetDateTime.ofInstant(
                        java.time.Instant.ofEpochSecond(apiTrade.getTime().getSeconds(), apiTrade.getTime().getNanos()),
                        ZoneOffset.UTC))
                .price(quotationToBigDecimal(apiTrade.getPrice()))
                .quantity(apiTrade.getQuantity())
                .direction(apiTrade.getDirection().getNumber() == 2
                        ? TradeEntity.TradeDirection.SELL
                        : TradeEntity.TradeDirection.BUY)
                .build();
    }

    private static BigDecimal quotationToBigDecimal(ru.tinkoff.piapi.contract.v1.Quotation quotation) {
        long units = quotation.getUnits();
        int nano = quotation.getNano();
        return BigDecimal.valueOf(units).add(BigDecimal.valueOf(nano, 9));
    }
}
