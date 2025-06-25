package org.example.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.example.postgres.entity.*;
import org.example.postgres.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@EnableScheduling
@Slf4j
public class StockDataRowScheduler {

    private final StockRepository stockRepository;
    private final CandleRepository candleRepository;
    private final OrderbookRepository orderbookRepository;
    private final TradeRepository tradeRepository;
    private final StockDataRepository stockDataRepository;

    @Autowired
    public StockDataRowScheduler(StockRepository stockRepository, CandleRepository candleRepository, OrderbookRepository orderbookRepository, TradeRepository tradeRepository, StockDataRepository stockDataRepository) {
        this.stockRepository = stockRepository;
        this.candleRepository = candleRepository;
        this.orderbookRepository = orderbookRepository;
        this.tradeRepository = tradeRepository;
        this.stockDataRepository = stockDataRepository;
    }

    @Scheduled(fixedDelayString = "#{aggregateScheduler.interval()}")
    private void aggregate() {
        List<StockEntity> stocks = stockRepository.getAll();
        stocks.forEach(stock -> {
            String figi = stock.getFigi();
                CandleEntity candle = candleRepository.getLastCandleByFigi(figi);
                if (candle.getRsi() == null || candle.getEma() == null || candle.getRsi() == null) {
                    return;
                }
                OffsetDateTime from = candle.getTime();
                OffsetDateTime to = from.plusSeconds(59).plusNanos(999999999);
                //TODO если бук из промежутка нет (стакан стоит на месте без новых заявок) то нужно брать последний старый
                //так же с торгами
                List<OrderbookEntity> orderbookEntities = orderbookRepository.findByFigiAndTimeRange(figi, from, to);

                long askVolume = orderbookEntities.stream()
                        .flatMap(orderbookEntity -> orderbookEntity.getAsks().stream())
                        .map(OrderbookEntity.Order::getQuantity).mapToLong(quantity -> quantity).sum();
                long bidVolume = orderbookEntities.stream()
                        .flatMap(orderbookEntity -> orderbookEntity.getBids().stream())
                        .mapToLong(OrderbookEntity.Order::getQuantity).sum();


                List<TradeEntity> tradeEntities = tradeRepository.findByFigiAndTimeRange(figi, from, to);

                long buyVolume = tradeEntities.stream()
                        .filter(tradeEntity -> tradeEntity.getDirection().equals(TradeEntity.TradeDirection.BUY))
                        .mapToLong(TradeEntity::getQuantity).sum();
                long sellVolume = tradeEntities.stream()
                        .filter(tradeEntity -> tradeEntity.getDirection().equals(TradeEntity.TradeDirection.SELL))
                        .mapToLong(TradeEntity::getQuantity).sum();

                StockDataEntity stockDataEntity = new StockDataEntity();
                stockDataEntity.setFigi(figi);
                stockDataEntity.setInstrumentUid(stock.getInstrumentUid());
                stockDataEntity.setTicker(stock.getTicker());
                stockDataEntity.setTime(from);
                stockDataEntity.setOpenPrice(candle.getOpenPrice());
                stockDataEntity.setHighPrice(candle.getHighPrice());
                stockDataEntity.setLowPrice(candle.getLowPrice());
                stockDataEntity.setClosePrice(candle.getClosePrice());
                stockDataEntity.setVolume(candle.getVolume());
                stockDataEntity.setAskVolume(askVolume);
                stockDataEntity.setBidVolume(bidVolume);
                stockDataEntity.setBuyVolume(buyVolume);
                stockDataEntity.setSellVolume(sellVolume);
                stockDataEntity.setRsi(candle.getRsi());
                stockDataEntity.setEma(candle.getEma());
                stockDataEntity.setMacd(candle.getMacd());

                stockDataRepository.save(stockDataEntity);


        });
    }
}
