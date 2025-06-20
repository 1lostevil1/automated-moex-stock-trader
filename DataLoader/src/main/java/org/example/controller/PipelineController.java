package org.example.controller;

import org.example.postgres.entity.StockEntity;
import org.example.repository.StockRepository;
import org.example.service.streamData.interfaces.DataStreamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@Controller
public class PipelineController {

    private final Map<String, DataStreamService> services;
    List<String> stocks;

    @Autowired
    public PipelineController(StockRepository stockRepository, Map<String, DataStreamService> services) {
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
}
