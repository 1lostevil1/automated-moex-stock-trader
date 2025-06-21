package org.example.scheduler;

import org.example.postgres.entity.ForecastRequed;
import org.example.postgres.repository.StockDataRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class KafkaSenderSchedule {
    private final KafkaTemplate<String, ForecastRequed> kafkaTemplate;
    private final StockDataRepository stockDataRepository;

    public KafkaSenderSchedule(KafkaTemplate<String, ForecastRequed> kafkaTemplate, StockDataRepository stockDataRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.stockDataRepository = stockDataRepository;
    }

    @Scheduled(fixedDelayString = "#{aggregateScheduler.interval()}")
    public void sendDataToKafka() {
        var figis = stockDataRepository.getAllFigi();
        for (var figi:figis) {
            var firecastRequedt = stockDataRepository.getByFigiWithLimit(figi,5);
            kafkaTemplate.send("forecast", firecastRequedt);
        }
    }
}
