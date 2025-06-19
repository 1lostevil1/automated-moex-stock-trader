package org.example.util.dtoMapper;

import org.example.postgres.entity.CandleEntity;
import ru.tinkoff.piapi.contract.v1.Candle;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class CandleMapper {

    public static CandleEntity mapApiCandleToEntity(Candle apiCandle) {
        return CandleEntity.builder()
                .figi(apiCandle.getFigi())
                .instrumentUid(apiCandle.getInstrumentUid())
                .time(OffsetDateTime.ofInstant(
                        java.time.Instant.ofEpochSecond(apiCandle.getTime().getSeconds(), apiCandle.getTime().getNanos()),
                        ZoneOffset.UTC))
                .openPrice(quotationToBigDecimal(apiCandle.getOpen()))
                .closePrice(quotationToBigDecimal(apiCandle.getClose()))
                .highPrice(quotationToBigDecimal(apiCandle.getHigh()))
                .lowPrice(quotationToBigDecimal(apiCandle.getLow()))
                .volume(apiCandle.getVolume())
                .build();
    }

    private static BigDecimal quotationToBigDecimal(ru.tinkoff.piapi.contract.v1.Quotation quotation) {
        long units = quotation.getUnits();
        int nano = quotation.getNano();
        return BigDecimal.valueOf(units).add(BigDecimal.valueOf(nano, 9));
    }
}