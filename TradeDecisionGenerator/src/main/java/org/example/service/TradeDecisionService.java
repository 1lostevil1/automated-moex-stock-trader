package org.example.service;

import org.example.message.ForecastResponse;
import org.example.postgres.entity.TradeDecisionDirection;
import org.example.postgres.entity.TradeDecisionEntity;
import org.example.postgres.repository.TradeDecisionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Service
public class TradeDecisionService {

    private final LastPriceCacheService lastPriceCache;
    private final TradeDecisionRepository tradeDecisionRepository;

    public TradeDecisionService(LastPriceCacheService lastPriceCache,
                                TradeDecisionRepository tradeDecisionRepository) {
        this.lastPriceCache = lastPriceCache;
        this.tradeDecisionRepository = tradeDecisionRepository;
    }

    public TradeDecisionEntity makeDecision(ForecastResponse forecastResponse) {
        String ticker = forecastResponse.ticker();
        double forecastPrice = forecastResponse.closePrice();

        Double lastPrice = lastPriceCache.getLastPrice(ticker);
        if (lastPrice == null) {
            return null;
        }

        TradeDecisionEntity prevDecision = tradeDecisionRepository.findByTicker(ticker);
        TradeDecisionDirection newDirection = calculateNewDirection(prevDecision, lastPrice, forecastPrice);

        if (newDirection == null || newDirection.isHold()) {
            return null;
        }

        double entryPrice = prevDecision != null ? prevDecision.getPrice().doubleValue() : lastPrice;
        double[] levels = calculateLevels(newDirection, prevDecision, entryPrice, lastPrice, forecastPrice);

        return buildAndSaveDecision(ticker, entryPrice, lastPrice, levels, newDirection);
    }

    private TradeDecisionDirection calculateNewDirection(TradeDecisionEntity prevDecision,
                                                         double lastPrice,
                                                         double forecastPrice) {
        // Новые позиции
        if (prevDecision == null) {
            return forecastPrice > lastPrice ?
                    TradeDecisionDirection.LONG :
                    TradeDecisionDirection.SHORT;
        }

        TradeDecisionDirection prevDir = prevDecision.getDirection();

        // Проверка стоп-лосса (используем getBaseDirection() для закрытия)
        if ((prevDir.isLong() && lastPrice <= prevDecision.getStopLoss().doubleValue()) ||
                (prevDir.isShort() && lastPrice >= prevDecision.getStopLoss().doubleValue())) {
            return prevDir == TradeDecisionDirection.LONG || prevDir == TradeDecisionDirection.LONG_HOLD ?
                    TradeDecisionDirection.LONG_HOLD : // Для закрытия LONG
                    TradeDecisionDirection.SHORT_HOLD; // Для закрытия SHORT
        }

        // Проверка достижения TP (переводим в HOLD режим)
        if ((prevDir.isLong() && lastPrice >= prevDecision.getTakeProfit().doubleValue()) ||
                (prevDir.isShort() && lastPrice <= prevDecision.getTakeProfit().doubleValue())) {
            return prevDir.isLong() ?
                    TradeDecisionDirection.LONG_HOLD :
                    TradeDecisionDirection.SHORT_HOLD;
        }

        // Разворот при противоположном прогнозе
        if ((prevDir.isLong() && forecastPrice < lastPrice) ||
                (prevDir.isShort() && forecastPrice > lastPrice)) {
            return forecastPrice > lastPrice ?
                    TradeDecisionDirection.LONG :
                    TradeDecisionDirection.SHORT;
        }

        // Сохранение текущего направления
        return prevDir;
    }

    private double[] calculateLevels(TradeDecisionDirection direction,
                                     TradeDecisionEntity prevDecision,
                                     double entryPrice,
                                     double lastPrice,
                                     double forecastPrice) {
        // Для новых позиций
        if (direction.isOpening()) {
            double stopLoss = direction.isLong() ?
                    entryPrice - (forecastPrice - entryPrice) * 0.5 :
                    entryPrice + (entryPrice - forecastPrice) * 0.5;
            return new double[]{forecastPrice, stopLoss};
        }

        // Для режима удержания (HOLD)
        if (direction.isHold()) {
            double initialTakeProfit = prevDecision.getTakeProfit().doubleValue();
            return new double[]{
                    forecastPrice, // Обновленный TP
                    initialTakeProfit // SL = предыдущий TP
            };
        }

        // Для других случаев (не должно происходить)
        return new double[]{
                prevDecision != null ? prevDecision.getTakeProfit().doubleValue() : lastPrice,
                prevDecision != null ? prevDecision.getStopLoss().doubleValue() : lastPrice
        };
    }

    private TradeDecisionEntity buildAndSaveDecision(String ticker,
                                                     double entryPrice,
                                                     double lastPrice,
                                                     double[] levels,
                                                     TradeDecisionDirection direction) {
        TradeDecisionEntity decision = TradeDecisionEntity.builder()
                .ticker(ticker)
                .price(BigDecimal.valueOf(entryPrice))
                .lastPrice(BigDecimal.valueOf(lastPrice))
                .takeProfit(BigDecimal.valueOf(levels[0]))
                .stopLoss(BigDecimal.valueOf(levels[1]))
                .direction(direction)
                .createdAt(OffsetDateTime.now())
                .build();

        tradeDecisionRepository.save(decision);
        return decision;
    }
}