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


    public TradeDecisionService(LastPriceCacheService lastPriceCache, TradeDecisionRepository tradeDecisionRepository) {
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

        TradeDecisionEntity prevDecision = tradeDecisionRepository.findByTicker(ticker);
        if (prevDecision == null) {
            prevDecision = tradeDecisionRepository.findByTicker(ticker);
        }

        TradeDecisionDirection newDirection;

        if (prevDecision != null) {
            TradeDecisionDirection prevDir = prevDecision.getDirection();

            if (prevDir.isLong()) {
                if (forecastPriceBD.compareTo(lastPrice) > 0) {
                    newDirection = TradeDecisionDirection.LONG_HOLD;
                } else if (forecastPriceBD.compareTo(lastPrice) < 0) {
                    newDirection = TradeDecisionDirection.SHORT;
                } else {
                    newDirection = TradeDecisionDirection.LONG_HOLD;
                }
            } else if (prevDir.isShort()) {
                if (forecastPriceBD.compareTo(lastPrice) < 0) {
                    newDirection = TradeDecisionDirection.SHORT_HOLD;
                } else if (forecastPriceBD.compareTo(lastPrice) > 0) {
                    newDirection = TradeDecisionDirection.LONG;
                } else {
                    newDirection = TradeDecisionDirection.SHORT_HOLD;
                }
            } else {
                if (forecastPriceBD.compareTo(lastPrice) > 0) {
                    newDirection = TradeDecisionDirection.LONG;
                } else if (forecastPriceBD.compareTo(lastPrice) < 0) {
                    newDirection = TradeDecisionDirection.SHORT;
                } else {
                    return null;
                }
            }
        } else {
            if (forecastPriceBD.compareTo(lastPrice) > 0) {
                newDirection = TradeDecisionDirection.LONG;
            } else if (forecastPriceBD.compareTo(lastPrice) < 0) {
                newDirection = TradeDecisionDirection.SHORT;
            } else {
                return null;
            }
        }

        BigDecimal takeProfit = forecastPriceBD;
        BigDecimal stopLoss;

        if (newDirection.isOpening()) {
            BigDecimal diff = takeProfit.subtract(lastPrice).abs();
            BigDecimal halfDiff = diff.multiply(BigDecimal.valueOf(0.5));

            if (newDirection.isLong()) {
                stopLoss = lastPrice.subtract(halfDiff);
            } else {
                stopLoss = lastPrice.add(halfDiff);
            }
        } else if (newDirection.isHold() && prevDecision != null) {
            if ((newDirection == TradeDecisionDirection.LONG_HOLD && prevDecision.getDirection() == TradeDecisionDirection.LONG) ||
                    (newDirection == TradeDecisionDirection.SHORT_HOLD && prevDecision.getDirection() == TradeDecisionDirection.SHORT)) {
                stopLoss = prevDecision.getTakeProfit();
                takeProfit = forecastPriceBD;
            } else {
                stopLoss = prevDecision.getStopLoss();
                takeProfit = prevDecision.getTakeProfit();
            }
        } else {
            BigDecimal diff = takeProfit.subtract(lastPrice).abs();
            BigDecimal halfDiff = diff.multiply(BigDecimal.valueOf(0.5));
            if (newDirection.isLong()) {
                stopLoss = lastPrice.subtract(halfDiff);
            } else {
                stopLoss = lastPrice.add(halfDiff);
            }
        }

        TradeDecisionEntity decision = TradeDecisionEntity.builder()
                .ticker(ticker)
                .price(lastPrice)
                .stopLoss(stopLoss)
                .takeProfit(takeProfit)
                .direction(newDirection)
                .createdAt(OffsetDateTime.now())
                .build();

        System.out.println(decision);
        tradeDecisionRepository.save(decision);

        return decision;
    }
}
