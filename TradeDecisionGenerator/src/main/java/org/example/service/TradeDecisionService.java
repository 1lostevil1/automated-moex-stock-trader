package org.example.service;

import org.example.message.ForecastResponse;
import org.example.postgres.entity.TradeDecisionDirection;
import org.example.postgres.entity.TradeDecisionEntity;
import org.example.postgres.repository.TradeDecisionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Service
public class TradeDecisionService {

    private final LastPriceCacheService lastPriceCache;
    private final TradeDecisionRepository tradeDecisionRepository;

    @Value("${strategy.min_reversal_profit:0.0025}")
    private double minReversalProfit;

    @Value("${strategy.risk_multiplier:0.5}")
    private double riskMultiplier;

    public TradeDecisionService(LastPriceCacheService lastPriceCache,
                                TradeDecisionRepository tradeDecisionRepository) {
        this.lastPriceCache = lastPriceCache;
        this.tradeDecisionRepository = tradeDecisionRepository;
    }

    public TradeDecisionEntity makeDecision(ForecastResponse forecastResponse) {
        String ticker = forecastResponse.ticker();
        double forecastPrice = forecastResponse.closePrice();

        Double lastPriceDouble = lastPriceCache.getLastPrice(ticker);
        if (lastPriceDouble == null) {
            return null;
        }
        double lastPrice = lastPriceDouble;
        double minProfitThreshold = lastPrice * minReversalProfit;

        TradeDecisionEntity prevDecision = tradeDecisionRepository.findByTicker(ticker);
        TradeDecisionDirection newDirection = null;
        double entryPrice = lastPrice;

        // 1. Определение направления позиции
        if (prevDecision == null) {
            if (forecastPrice > lastPrice) {
                newDirection = TradeDecisionDirection.LONG;
            } else if (forecastPrice < lastPrice) {
                newDirection = TradeDecisionDirection.SHORT;
            }
        } else {
            entryPrice = prevDecision.getPrice().doubleValue();
            TradeDecisionDirection prevDir = prevDecision.getDirection();

            double potentialProfit = 0.0;
            if (prevDir.isLong() && forecastPrice < lastPrice) {
                potentialProfit = lastPrice - forecastPrice;
            } else if (prevDir.isShort() && forecastPrice > lastPrice) {
                potentialProfit = forecastPrice - lastPrice;
            }

            if (prevDir.isLong()) {
                if (forecastPrice > lastPrice) {
                    newDirection = TradeDecisionDirection.LONG_HOLD;
                } else if (potentialProfit >= minProfitThreshold) {
                    newDirection = TradeDecisionDirection.SHORT;
                    entryPrice = lastPrice;
                } else {
                    newDirection = TradeDecisionDirection.LONG_HOLD;
                }
            } else if (prevDir.isShort()) {
                if (forecastPrice < lastPrice) {
                    newDirection = TradeDecisionDirection.SHORT_HOLD;
                } else if (potentialProfit >= minProfitThreshold) {
                    newDirection = TradeDecisionDirection.LONG;
                    entryPrice = lastPrice;
                } else {
                    newDirection = TradeDecisionDirection.SHORT_HOLD;
                }
            }
        }

        if (newDirection == null) {
            return null;
        }

        // 2. Расчет уровней takeProfit и stopLoss
        double takeProfit;
        double stopLoss;
        double priceDiff = Math.abs(forecastPrice - lastPrice);

        if (newDirection.isOpening()) {
            // Новая позиция
            takeProfit = forecastPrice;
            if (newDirection.isLong()) {
                stopLoss = lastPrice - (priceDiff * riskMultiplier);
            } else {
                stopLoss = lastPrice + (priceDiff * riskMultiplier);
            }
        } else {
            // Удержание позиции
            double prevTakeProfit = prevDecision.getTakeProfit().doubleValue();
            double prevStopLoss = prevDecision.getStopLoss().doubleValue();

            if (newDirection.isLong()) {
                // Для LONG
                if (forecastPrice > prevTakeProfit) {
                    takeProfit = forecastPrice;
                    stopLoss = prevTakeProfit;
                } else if (forecastPrice > entryPrice) {
                    takeProfit = forecastPrice;
                    double diff = Math.abs(forecastPrice - lastPrice);
                    stopLoss = lastPrice - (diff * riskMultiplier);
                } else {
                    takeProfit = prevTakeProfit;
                    stopLoss = prevStopLoss;
                }
            } else {
                // Для SHORT
                if (forecastPrice < prevTakeProfit) {
                    takeProfit = forecastPrice;
                    stopLoss = prevTakeProfit;
                } else if (forecastPrice < entryPrice) {
                    takeProfit = forecastPrice;
                    double diff = Math.abs(forecastPrice - lastPrice);
                    stopLoss = lastPrice + (diff * riskMultiplier);
                } else {
                    takeProfit = prevTakeProfit;
                    stopLoss = prevStopLoss;
                }
            }
        }

        // 3. Создание и сохранение решения
        TradeDecisionEntity decision = TradeDecisionEntity.builder()
                .ticker(ticker)
                .price(BigDecimal.valueOf(entryPrice))
                .lastPrice(BigDecimal.valueOf(lastPrice))
                .stopLoss(BigDecimal.valueOf(stopLoss))
                .takeProfit(BigDecimal.valueOf(takeProfit))
                .direction(newDirection)
                .createdAt(OffsetDateTime.now())
                .build();

        tradeDecisionRepository.save(decision);
        return decision;
    }
}