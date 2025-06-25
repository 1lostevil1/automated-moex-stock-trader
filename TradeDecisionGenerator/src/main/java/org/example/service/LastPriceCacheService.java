package org.example.service;

import lombok.AllArgsConstructor;
import org.example.postgres.entity.StockEntity;
import org.example.postgres.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class LastPriceCacheService {

    private final ConcurrentMap<String, Double> lastPriceMap = new ConcurrentHashMap<>();
    private final Map<String, String > figiTicker = new HashMap<>();

    @Autowired
    public  LastPriceCacheService(StockRepository stockRepository){
        List<StockEntity> stocks = stockRepository.getAll();
        for(StockEntity stock : stocks) figiTicker.put(stock.getTicker(),stock.getFigi());
    }


    public void updateLastPrice(String figi, Double price) {
        if (figi == null || price == null) {
            throw new IllegalArgumentException("Ticker and price must not be null");
        }
        lastPriceMap.put(figiTicker.get(figi), price);
    }

    public Double getLastPrice(String ticker) {
        if (ticker == null) {
            throw new IllegalArgumentException("Ticker must not be null");
        }
        return lastPriceMap.get(figiTicker.get(ticker));
    }
}
