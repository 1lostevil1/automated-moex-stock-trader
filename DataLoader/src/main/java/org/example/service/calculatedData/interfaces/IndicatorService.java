package org.example.service.calculatedData.interfaces;

import ru.tinkoff.piapi.contract.v1.Candle;

import java.util.List;

public interface IndicatorService {

    double calculate(List<Candle> candles);
}
