package org.example.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class LastPriceCacheService {

    private final ConcurrentMap<String, Double> lastPriceMap = new ConcurrentHashMap<>();


    public void updateLastPrice(String figi, Double price) {
        if (figi == null || price == null) {
            throw new IllegalArgumentException("Ticker and price must not be null");
        }
        lastPriceMap.put(figi, price);
    }

    public Double getLastPrice(String figi) {
        if (figi == null) {
            throw new IllegalArgumentException("Ticker must not be null");
        }
        return lastPriceMap.get(figi);
    }
}
