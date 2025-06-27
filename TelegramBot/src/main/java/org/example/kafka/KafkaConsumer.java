package org.example.kafka;

import com.pengrad.telegrambot.request.SendMessage;
import org.example.controller.TelegramBotController;
import org.example.kafka.message.KafkaMessage;
import org.example.postgres.entity.TradeDecisionEntity;
import org.example.postgres.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class KafkaConsumer {
    private final UserRepository repository;
    private final ExecutorService executorService = Executors.newFixedThreadPool(20);
    private final TelegramBotController bot;
    private static final String MESSAGE_TEMPLATE = """
        Акция с тикером: %s\n
        Цена входа: %s\n
        Цена сейчас: %s\n
        TP: %s\n
        SL: %s\n
        Направление: %s\n
    """;

    @Autowired
    public KafkaConsumer(TelegramBotController bot, UserRepository repository) {
        this.bot = bot;
        this.repository = repository;
    }

    @KafkaListener(topics = "${kafka.topic.name}")
    public void listen(TradeDecisionEntity tradeDecision) {
        var users = repository.getUsers(tradeDecision.getTicker());
        executorService.submit(() -> {
            try {
                String text = String.format(MESSAGE_TEMPLATE,
                        tradeDecision.getTicker(),
                        tradeDecision.getPrice().toString(),
                        tradeDecision.getLastPrice().toString(),
                        tradeDecision.getTakeProfit().toString(),
                        tradeDecision.getStopLoss().toString(),
                        tradeDecision.getDirection().name());
                System.out.println(text);
                for (var user : users) {
                    bot.sendNotification(new SendMessage(user, text));
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });

    }
}
