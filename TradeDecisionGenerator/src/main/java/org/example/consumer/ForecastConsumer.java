package org.example.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.message.ForecastResponse;
import org.example.postgres.entity.TradeDecisionEntity;
import org.example.service.TradeDecisionService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ForecastConsumer {

    private final TradeDecisionService tradeDecisionService;
    private final KafkaTemplate<String, TradeDecisionEntity> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String TRADE_DECISION_TOPIC = "tradeRequest";

    public ForecastConsumer(TradeDecisionService tradeDecisionService,
                            KafkaTemplate<String, TradeDecisionEntity> kafkaTemplate) {
        this.tradeDecisionService = tradeDecisionService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "forecastRequest", groupId = "tradeGroup")
    public void listen(String message) {
        try {
            ForecastResponse forecastResponse = objectMapper.readValue(message, ForecastResponse.class);

            TradeDecisionEntity tradeDecision = tradeDecisionService.makeDecision(forecastResponse);

            if (tradeDecision != null) {
                kafkaTemplate.send(TRADE_DECISION_TOPIC, tradeDecision.getTicker(), tradeDecision);
            }
        } catch (Exception e) {
            // Логирование ошибки
            e.printStackTrace();
        }
    }
}
