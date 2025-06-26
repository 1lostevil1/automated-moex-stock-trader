package org.example.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.message.ForecastRequest;
import org.example.message.ForecastResponse;
import org.example.postgres.entity.TradeDecisionEntity;
import org.example.service.TradeDecisionService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
public class ForecastConsumer {

    private final TradeDecisionService tradeDecisionService;
    private final ExecutorService executorService = Executors.newFixedThreadPool(20);
    private final KafkaTemplate<String, TradeDecisionEntity> kafkaTemplate;

    private static final String TRADE_DECISION_TOPIC = "tradeResponse";

    public ForecastConsumer(TradeDecisionService tradeDecisionService,
                            KafkaTemplate<String, TradeDecisionEntity> kafkaTemplate) {
        this.tradeDecisionService = tradeDecisionService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "forecastResponse", groupId = "tradeGroup")
    public void listen(ForecastResponse forecastResponse) {

            executorService.submit(() -> {
                try {
                    TradeDecisionEntity tradeDecision = tradeDecisionService.makeDecision(forecastResponse);
                    if (tradeDecision != null) {
                        kafkaTemplate.send(TRADE_DECISION_TOPIC, tradeDecision);
                    }
                } catch (Exception ignored) {
                }
            });

    }
}
