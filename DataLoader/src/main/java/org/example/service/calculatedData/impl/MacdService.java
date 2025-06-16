package org.example.service.calculatedData.impl;

import org.example.service.calculatedData.interfaces.IndicatorService;
import org.springframework.stereotype.Service;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBar;
import org.ta4j.core.BaseBarSeries;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.MACDIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;

import java.util.List;

@Service
public class MacdService implements IndicatorService {

    /**
     * Рассчитывает MACD по списку свечей
     *
     * @param candles        список свечей, отсортированных по времени от старых к новым
     * @param shortEmaPeriod период короткой EMA (обычно 12)
     * @param longEmaPeriod  период длинной EMA (обычно 26)
     * @param signalPeriod   период сигнальной линии EMA (обычно 9)
     */


    @Override
    public double calculate(List<Candle> candles) {
        if (candles == null || candles.size() < longEmaPeriod + signalPeriod) {
            throw new IllegalArgumentException("Недостаточно свечей для расчёта MACD");
        }

        BarSeries series = new BaseBarSeries("series");

        for (Candle candle : candles) {
            Bar bar = new BaseBar(
                    Duration.ofMinutes(1),
                    candle.getTime().atZone(ZoneId.systemDefault()),
                    candle.getOpen(),
                    candle.getHigh(),
                    candle.getLow(),
                    candle.getClose(),
                    candle.getVolume()
            );
            series.addBar(bar);
        }

        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
        MACDIndicator macd = new MACDIndicator(closePrice, shortEmaPeriod, longEmaPeriod);
        EMAIndicator signalLine = new EMAIndicator(macd, signalPeriod);

        int lastIndex = series.getEndIndex();

        return macd.getValue(lastIndex).doubleValue();
    }
}
