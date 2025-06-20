package org.example.service.calculatedData.impl;

import org.example.postgres.entity.CandleEntity;
import org.example.service.calculatedData.interfaces.IndicatorService;
import org.springframework.stereotype.Service;
import org.ta4j.core.Bar;
import org.ta4j.core.BaseBar;
import org.ta4j.core.BaseBarSeries;
import org.ta4j.core.indicators.MACDIndicator;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;

import java.math.BigDecimal;
import java.time.*;
import java.util.List;

@Service("MACD")
public class MacdService implements IndicatorService {

    private final int SHORT_EMA_PERIOD = 12;
    private final int LONG_EMA_PERIOD = 26;
    private final int SIGNAL_PERIOD = 9;

    @Override
    public double calculate(List<CandleEntity> candles) {
        if (candles == null || candles.size() < LONG_EMA_PERIOD + SIGNAL_PERIOD) {
            throw new IllegalArgumentException("Недостаточно данных для расчёта MACD");
        }

        BaseBarSeries series = new BaseBarSeries();

        for (CandleEntity candle : candles) {
            ZonedDateTime endTime = toZonedDateTime(candle.getTime());

            double openPrice = candle.getOpenPrice().doubleValue();
            double highPrice = candle.getHighPrice().doubleValue();
            double lowPrice = candle.getLowPrice().doubleValue();
            double closePrice = candle.getClosePrice().doubleValue();
            long volume = candle.getVolume();

            Bar bar = createBar(series, endTime, openPrice, highPrice, lowPrice, closePrice, volume);
            series.addBar(bar);
        }

        ClosePriceIndicator closePriceIndicator = new ClosePriceIndicator(series);

        MACDIndicator macd = new MACDIndicator(closePriceIndicator, SHORT_EMA_PERIOD, LONG_EMA_PERIOD);
        EMAIndicator signal = new EMAIndicator(macd, SIGNAL_PERIOD);

        double macdValue = macd.getValue(series.getEndIndex()).doubleValue();
        double signalValue = signal.getValue(series.getEndIndex()).doubleValue();

        return macdValue - signalValue;
    }

    private ZonedDateTime toZonedDateTime(OffsetDateTime dateTime) {
        return dateTime.toLocalDateTime().atZone(ZoneId.of("UTC"));
    }

    private Bar createBar(BaseBarSeries series, ZonedDateTime endTime,
                          double open, double high, double low, double close, long volume) {

        var openNum = series.numOf(open);
        var highNum = series.numOf(high);
        var lowNum = series.numOf(low);
        var closeNum = series.numOf(close);
        var volumeNum = series.numOf(volume);

        return BaseBar.builder()
                .timePeriod(Duration.ofMinutes(1))
                .endTime(endTime)
                .openPrice(openNum)
                .highPrice(highNum)
                .lowPrice(lowNum)
                .closePrice(closeNum)
                .volume(volumeNum)
                .build();
    }

    @Override
    public int getCount() {
        return LONG_EMA_PERIOD + SIGNAL_PERIOD;
    }
}