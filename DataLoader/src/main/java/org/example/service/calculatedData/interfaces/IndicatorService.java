package org.example.service.calculatedData.interfaces;

import java.util.List;

public interface IndicatorService {

    double calculate(List<Candle> candles);
}
