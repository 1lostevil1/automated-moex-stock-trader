package org.example.consumer;

import lombok.AllArgsConstructor;
import org.example.postgres.entity.TradeDecisionEntity;
import org.example.postgres.repository.ForecastRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@AllArgsConstructor
public class ForecastResponseListener {

    private SimpMessagingTemplate messagingTemplate;
    private final ForecastRepository forecastRepository;
    private final int AGO = 4;


    @KafkaListener(topics = "tradeResponse", groupId = "userGroup")
    public void listen(TradeDecisionEntity tradeDecision) {
        messagingTemplate.convertAndSend("/topic/forecastResponse",
                forecastRepository.getByTickerFromTime(tradeDecision.getTicker(), OffsetDateTime.now().minusHours(AGO)));
        messagingTemplate.convertAndSend("/topic/tradeDecision", tradeDecision);
    }
}
