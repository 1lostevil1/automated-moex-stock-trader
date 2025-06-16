package org.example.scheduled;

import org.example.config.ApplicationConfig;
import org.example.config.InvestApiConfig;
import org.example.service.calculatedData.impl.MacdService;
import org.example.service.calculatedData.impl.RsiService;
import org.example.service.calculatedData.interfaces.IndicatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.tinkoff.piapi.contract.v1.Candle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class IndicatorScheduler {

    private final Map<String, IndicatorService> serviceMap;

    @Autowired
    public IndicatorScheduler(RsiService rsiService, MacdService macdService) {
        serviceMap = new HashMap<>();
        serviceMap.put(InvestApiConfig.RSI, rsiService);
        serviceMap.put(InvestApiConfig.MACD, macdService);
    }

    @Scheduled(fixedDelayString = "#{scheduler.interval}")
    public void calculateIndicators() {
        // Получаем последние свечи (например, за последние 5 минут) из бд!!!
        List<Candle> candles = new ArrayList<>();

        if (candles == null || candles.isEmpty()) {
            System.out.println("Нет свечей для расчёта индикаторов");
            return;
        }

        for (var pair : serviceMap.entrySet()) {
            String indicator = pair.getKey();
            IndicatorService service = pair.getValue();

            double indicatorVal = service.calculate(candles);

            System.out.println(indicator + "  " + indicatorVal);
        }
    }
}
