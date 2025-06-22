package org.example.scheduler;

import org.example.message.ForecastRequest;
import org.example.postgres.entity.StockEntity;
import org.example.postgres.repository.StockDataRepository;
import org.example.postgres.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class KafkaSenderScheduler {
    private final KafkaTemplate<String, ForecastRequest> kafkaTemplate;
    private final StockDataRepository stockDataRepository;
    private final StockRepository stockRepository;

    @Autowired
    public KafkaSenderScheduler(KafkaTemplate<String, ForecastRequest> kafkaTemplate, StockDataRepository stockDataRepository, StockRepository stockRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.stockDataRepository = stockDataRepository;
        this.stockRepository = stockRepository;
    }

    @Scheduled(fixedDelayString = "#{sendScheduler.interval()}")
    public void sendDataToKafka() {
        var figis = stockRepository.getAll().stream().map(StockEntity::getFigi).toList();
        for (var figi : figis) {
            var forecastRequestList = stockDataRepository.findByFigiFromTime(figi, OffsetDateTime.now().minusMinutes(5));
            kafkaTemplate.send("forecastRequest", new ForecastRequest(figi, forecastRequestList));
        }
    }
}
