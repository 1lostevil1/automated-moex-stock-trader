package org.example.controller;

import org.example.postgres.entity.StockEntity;
import org.example.postgres.repository.StockRepository;
import org.example.service.streamData.interfaces.DataStreamService;
import org.example.util.finder.FigiFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
public class PipelineController {

    private final FigiFinder figiFinder;
    private final StockRepository stockRepository;
    private final Map<String, DataStreamService> services;
    List<String> stocks;

    @Autowired
    public PipelineController(FigiFinder figiFinder, StockRepository stockRepository1, StockRepository stockRepository, Map<String, DataStreamService> services) {
        this.figiFinder = figiFinder;
        this.stockRepository = stockRepository1;
        this.services = services;
        stocks = stockRepository.getAll().stream().map(StockEntity::getFigi).toList();
    }

    @GetMapping("/start")
    public void subscribe() {
        services.forEach((key, value) -> {
            value.subscribe(stocks);
        });
    }

    @GetMapping("/stop")
    public void unsubscribe() {
        services.forEach((key, value) -> {
            value.unsubscribe(stocks);
        });
    }

    @PostMapping("/add")
    public void saveStock(@RequestParam("ticker") String ticker) {
        String figi = figiFinder.getFigiByTicker(ticker);

        StockEntity stockEntity = new StockEntity();
        stockEntity.setTicker(ticker);
        stockEntity.setFigi(figi);
        stockEntity.setInstrumentUid(ticker);
        stockEntity.setName(ticker);

        stockRepository.save(stockEntity);
        stocks.add(figi);
    }
}
