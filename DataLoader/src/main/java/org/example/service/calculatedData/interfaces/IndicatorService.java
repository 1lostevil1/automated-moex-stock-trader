package org.example.service.calculatedData.interfaces;

import org.example.postgres.entity.CandleEntity;

import java.util.List;

public interface IndicatorService {

    double calculate(List<CandleEntity> candles);
    int getCount();
}
