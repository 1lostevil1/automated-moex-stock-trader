package org.example.scheduled;

import lombok.extern.slf4j.Slf4j;
import org.example.postgres.entity.CandleEntity;
import org.example.postgres.entity.StockEntity;
import org.example.repository.CandleRepository;
import org.example.repository.StockRepository;
import org.example.service.calculatedData.interfaces.IndicatorService;
import org.example.client.CandleClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@EnableScheduling
@Component
@Slf4j
public class IndicatorScheduler {
    private final StockRepository stockRepository;
    private final CandleRepository candleRepository;
    private final CandleClient candleClient;
    private final Map<String, IndicatorService> indicatorServices;

    @Autowired
    public IndicatorScheduler(StockRepository stockRepository, CandleRepository candleRepository, CandleClient candleClient, Map<String, IndicatorService> indicatorServices) {
        this.stockRepository = stockRepository;
        this.candleRepository = candleRepository;
        this.candleClient = candleClient;
        this.indicatorServices = indicatorServices;
    }


    @Scheduled(fixedDelayString = "#{scheduler.interval}")
    private void task(){

    List<StockEntity> stocks = stockRepository.getAll();
    for (StockEntity stock : stocks) {
        String figi = stock.getFigi();

        for(var kv : indicatorServices.entrySet()){
            String name = kv.getKey();
            IndicatorService indicatorService = indicatorServices.get(name);
            int count = indicatorService.getCount();

            log.info(name);
           List<CandleEntity> dbCandles = candleRepository.getByFigiWithLimit(figi, count);
           try {
               double value = indicatorService.calculate(dbCandles);
               switch (name){
                   case "RSI": {
                       candleRepository.updateRsi(figi, BigDecimal.valueOf(value));
                       break;
                   }
                   case "MACD": {
                       candleRepository.updateMacd(figi,BigDecimal.valueOf(value));
                       break;
                   }
                   case "EMA": {
                       candleRepository.updateEma(figi,BigDecimal.valueOf(value));
                       break;
                   }
               }

           } catch (IllegalArgumentException e){
               log.info("свечек не хватило бедолаге");
           }
        };

    }

    }

}
