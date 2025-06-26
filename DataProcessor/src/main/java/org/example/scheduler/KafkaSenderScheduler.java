package org.example.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.example.message.ForecastRequest;
import org.example.postgres.entity.StockDataEntity;
import org.example.postgres.entity.StockEntity;
import org.example.postgres.repository.StockDataRepository;
import org.example.postgres.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.*;

@Service
@Slf4j
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
        Queue<String> tickerQueue = new LinkedList<>(stockRepository.getAll().stream()
                .map(StockEntity::getTicker)
                .toList());

        Map<String, Integer> retryCount = new HashMap<>();
        final int MAX_RETRIES = 30000;

        while (!tickerQueue.isEmpty()) {

            String ticker = tickerQueue.poll();
            OffsetDateTime time = OffsetDateTime.now().minusHours(3).minusMinutes(11);
            List<StockDataEntity> forecastRequestList = stockDataRepository.findByTickerFromTime(ticker,
                    time);

            if (forecastRequestList.size() == 11) forecastRequestList.removeFirst();
            boolean hasNullIndicators = forecastRequestList.stream()
                    .anyMatch(data -> data.getRsi() == null || data.getMacd() == null || data.getEma() == null);

            if (hasNullIndicators) {
                int count = retryCount.getOrDefault(ticker, 0);
                if (count < MAX_RETRIES) {
                    retryCount.put(ticker, count + 1);
                    tickerQueue.offer(ticker); // возвращаем тикер в очередь
                    log.info("Retrying ticker {} (attempt {}) due to null indicators", ticker, count + 1);
                } else {
                    log.warn("Max retries reached for ticker {}. Skipping.", ticker);
                }
            } else {
                if (forecastRequestList.size() < 10) {
                    log.info(ticker + "  " + forecastRequestList.size());
                    log.info("мало данных для предсказания");
                    continue;
                }
                ForecastRequest forecastRequest = new ForecastRequest(ticker, forecastRequestList);
                kafkaTemplate.send("forecastRequest", forecastRequest);

            }
        }
    }
}
