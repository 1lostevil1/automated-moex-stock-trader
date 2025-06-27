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

        // Не создаем новое решение если:
        // - Закрытие по SL (newDirection == null)
        // - Удержание позиции без изменений (newDirection.isHold() и нет изменений в уровнях)
        if (newDirection == null || (prevDecision != null && newDirection == prevDecision.getDirection())) {
            return null;
        }

        double entryPrice = prevDecision != null ? prevDecision.getPrice().doubleValue() : lastPrice;
        double[] levels = calculateLevels(newDirection, prevDecision, entryPrice, lastPrice, forecastPrice);

        return buildAndSaveDecision(ticker, entryPrice, lastPrice, levels, newDirection);
    }

    private TradeDecisionDirection calculateNewDirection(TradeDecisionEntity prevDecision,
                                                         double lastPrice,
                                                         double forecastPrice) {
        // 1. Новая позиция (если нет предыдущего решения)
        if (prevDecision == null) {
            return forecastPrice > lastPrice ?
                    TradeDecisionDirection.LONG :
                    TradeDecisionDirection.SHORT;
        }

        TradeDecisionDirection currentDir = prevDecision.getDirection();
        double stopLoss = prevDecision.getStopLoss().doubleValue();

        // 2. Проверка стоп-лосса
        if ((currentDir.isLong() && lastPrice <= stopLoss) ||
                (currentDir.isShort() && lastPrice >= stopLoss)) {
            return null; // Закрытие позиции
        }

        // 3. Удержание позиции
        if (shouldHoldPosition(currentDir, lastPrice, forecastPrice, stopLoss)) {
            return currentDir.isLong() ?
                    TradeDecisionDirection.LONG_HOLD :
                    TradeDecisionDirection.SHORT_HOLD;
        }

        // 4. Разворот позиции
        return forecastPrice > lastPrice ?
                TradeDecisionDirection.LONG :
                TradeDecisionDirection.SHORT;
    }

    private boolean shouldHoldPosition(TradeDecisionDirection currentDir,
                                       double lastPrice,
                                       double forecastPrice,
                                       double stopLoss) {
        // 1. Прогноз согласуется с текущим направлением
        boolean forecastAgrees = (currentDir.isLong() && forecastPrice > lastPrice) ||
                (currentDir.isShort() && forecastPrice < lastPrice);

        // 2. Прогноз нейтрален (цена не изменилась)
        boolean forecastNeutral = forecastPrice == lastPrice;

        // 3. Цена еще не достигла SL
        boolean notAtStopLoss = (currentDir.isLong() && lastPrice > stopLoss) ||
                (currentDir.isShort() && lastPrice < stopLoss);

        return forecastAgrees || forecastNeutral || notAtStopLoss;
    }

    private double[] calculateLevels(TradeDecisionDirection direction,
                                     TradeDecisionEntity prevDecision,
                                     double entryPrice,
                                     double lastPrice,
                                     double forecastPrice) {
        // 1. Закрытие позиции (не используется, так как возвращается null)
        if (direction == null) {
            return new double[]{lastPrice, lastPrice};
        }

        // 2. Новая позиция
        if (direction.isOpening()) {
            double takeProfit = forecastPrice;
            double stopLoss;

            if (direction.isLong()) {
                // LONG: SL = entry - 50% расстояния до TP
                stopLoss = entryPrice - (takeProfit - entryPrice) / 2;
            } else {
                // SHORT: SL = entry + 50% расстояния до TP
                stopLoss = entryPrice + (entryPrice - takeProfit) / 2;
            }

            return new double[]{takeProfit, stopLoss};
        }

        // 3. Режим удержания
        if (direction.isHold()) {
            if (prevDecision != null) {
                // Проверяем, достигнут ли старый Take Profit
                if (isTakeProfitReached(prevDecision, lastPrice)) {
                    // Сдвигаем SL на предыдущий TP, TP на новый прогноз
                    return new double[]{
                            forecastPrice, // новый TP
                            prevDecision.getTakeProfit().doubleValue() // новый SL = старый TP
                    };
                } else {
                    // Не достигли TP → оставляем SL, обновляем TP
                    return new double[]{
                            forecastPrice, // новый TP
                            prevDecision.getStopLoss().doubleValue() // SL без изменений
                    };
                }
            }
        }

        // 4. Без изменений или ошибка
        return new double[]{lastPrice, lastPrice};
    }

    private boolean isTakeProfitReached(TradeDecisionEntity prevDecision, double lastPrice) {
        if (prevDecision == null) return false;
        TradeDecisionDirection direction = prevDecision.getDirection();
        BigDecimal takeProfit = prevDecision.getTakeProfit();

        if (direction.isLong()) {
            return lastPrice >= takeProfit.doubleValue();
        } else if (direction.isShort()) {
            return lastPrice <= takeProfit.doubleValue();
        }
        return false;
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