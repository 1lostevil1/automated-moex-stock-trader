package org.example.service.calculatedData.impl;

import org.example.service.calculatedData.interfaces.IndicatorService;
import org.springframework.stereotype.Service;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBar;
import org.ta4j.core.BaseBarSeries;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;

import java.util.List;

@Service
public class RsiService implements IndicatorService {

    private static final int RSI_PERIOD = 14;

    public double calculate(List<Candle> candles) {
        if (candles == null || candles.size() < RSI_PERIOD + 1) {
            throw new IllegalArgumentException("Недостаточно свечей для расчёта RSI");
        }

        BarSeries series = new BaseBarSeries("series");

        for (Candle candle : candles) {
            Bar bar = new BaseBar(
                    candle.getTime(),
                    candle.getOpen(),
                    candle.getHigh(),
                    candle.getLow(),
                    candle.getClose(),
                    candle.getVolume()
            );
            series.addBar(bar);
        }

        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
        RSIIndicator rsi = new RSIIndicator(closePrice, RSI_PERIOD);

        int lastIndex = series.getEndIndex();
        return rsi.getValue(lastIndex).doubleValue();
    }
}
