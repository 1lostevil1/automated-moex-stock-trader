package org.example.service;

import org.example.message.ForecastResponse;
import org.example.postgres.entity.TradeDecisionDirection;
import org.example.postgres.entity.TradeDecisionEntity;
import org.example.postgres.repository.TradeDecisionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;

@Service
public class TradeDecisionService {

    private final LastPriceCacheService lastPriceCache;
    private final TradeDecisionRepository tradeDecisionRepository;

    @Value("${strategy.min_reversal_profit:0.0025}")
    private BigDecimal minReversalProfit;

    @Value("${strategy.risk_multiplier:0.5}")
    private BigDecimal riskMultiplier;

    public TradeDecisionService(LastPriceCacheService lastPriceCache,
                                TradeDecisionRepository tradeDecisionRepository) {
        this.lastPriceCache = lastPriceCache;
        this.tradeDecisionRepository = tradeDecisionRepository;
    }

    public TradeDecisionEntity makeDecision(ForecastResponse forecastResponse) {
        String ticker = forecastResponse.ticker();
        BigDecimal forecastPriceBD = BigDecimal.valueOf(forecastResponse.closePrice());

        Double lastPriceDouble = lastPriceCache.getLastPrice(ticker);
        if (lastPriceDouble == null) {
            return null; // Нет текущей цены — решение невозможно
        }
        BigDecimal lastPrice = BigDecimal.valueOf(lastPriceDouble);
        BigDecimal minProfitThreshold = lastPrice.multiply(minReversalProfit);

        TradeDecisionEntity prevDecision = tradeDecisionRepository.findByTicker(ticker);
        TradeDecisionDirection newDirection = null;
        BigDecimal entryPrice = lastPrice; // По умолчанию для новых позиций

        // 1. Определение направления с учетом минимальной прибыли для разворота
        if (prevDecision == null) {
            // Нет предыдущей позиции
            if (forecastPriceBD.compareTo(lastPrice) > 0) {
                newDirection = TradeDecisionDirection.LONG;
            } else if (forecastPriceBD.compareTo(lastPrice) < 0) {
                newDirection = TradeDecisionDirection.SHORT;
            }
        } else {
            entryPrice = prevDecision.getPrice(); // Сохраняем цену входа
            TradeDecisionDirection prevDir = prevDecision.getDirection();

            // Расчет потенциальной прибыли для разворота
            BigDecimal potentialProfit = BigDecimal.ZERO;
            if (prevDir.isLong() && forecastPriceBD.compareTo(lastPrice) < 0) {
                potentialProfit = lastPrice.subtract(forecastPriceBD);
            } else if (prevDir.isShort() && forecastPriceBD.compareTo(lastPrice) > 0) {
                potentialProfit = forecastPriceBD.subtract(lastPrice);
            }

            // Логика принятия решения
            if (prevDir.isLong()) {
                if (forecastPriceBD.compareTo(lastPrice) > 0) {
                    newDirection = TradeDecisionDirection.LONG_HOLD;
                } else if (potentialProfit.compareTo(minProfitThreshold) >= 0) {
                    newDirection = TradeDecisionDirection.SHORT;
                    entryPrice = lastPrice; // Сброс цены входа для новой позиции
                } else {
                    newDirection = TradeDecisionDirection.LONG_HOLD;
                }
            } else if (prevDir.isShort()) {
                if (forecastPriceBD.compareTo(lastPrice) < 0) {
                    newDirection = TradeDecisionDirection.SHORT_HOLD;
                } else if (potentialProfit.compareTo(minProfitThreshold) >= 0) {
                    newDirection = TradeDecisionDirection.LONG;
                    entryPrice = lastPrice; // Сброс цены входа для новой позиции
                } else {
                    newDirection = TradeDecisionDirection.SHORT_HOLD;
                }
            }
        }

        if (newDirection == null) {
            return null; // Не определено направление
        }

        // 2. Расчет уровней стоп-лосса и тейк-профита
        BigDecimal takeProfit = forecastPriceBD;
        BigDecimal stopLoss = null;

        // Расстояние между текущей ценой и прогнозом
        BigDecimal priceDiff = forecastPriceBD.subtract(lastPrice).abs();

        // Логика для новых позиций
        if (newDirection.isOpening()) {
            if (newDirection.isLong()) {
                stopLoss = lastPrice.subtract(priceDiff.multiply(riskMultiplier));
            } else {
                stopLoss = lastPrice.add(priceDiff.multiply(riskMultiplier));
            }
        }
        // Логика для удержания позиций
        else {
            // Если цена превысила предыдущий тейк-профит
            if (prevDecision != null) {
                if (newDirection.isLong() && lastPrice.compareTo(prevDecision.getTakeProfit()) > 0) {
                    stopLoss = prevDecision.getTakeProfit(); // Перенос стопа на предыдущий TP
                } else if (newDirection.isShort() && lastPrice.compareTo(prevDecision.getTakeProfit()) < 0) {
                    stopLoss = prevDecision.getTakeProfit(); // Перенос стопа на предыдущий TP
                }
                // Стандартный расчет стоп-лосса
                else {
                    if (newDirection.isLong()) {
                        stopLoss = lastPrice.subtract(priceDiff.multiply(riskMultiplier));
                    } else {
                        stopLoss = lastPrice.add(priceDiff.multiply(riskMultiplier));
                    }
                }
            }
        }

        // 3. Создание и сохранение решения
        TradeDecisionEntity decision = TradeDecisionEntity.builder()
                .ticker(ticker)
                .price(entryPrice)
                .lastPrice(lastPrice)
                .stopLoss(stopLoss)
                .takeProfit(takeProfit)
                .direction(newDirection)
                .createdAt(OffsetDateTime.now())
                .build();

        tradeDecisionRepository.save(decision);
        return decision;
    }
}