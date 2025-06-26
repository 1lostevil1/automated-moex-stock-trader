package org.example.kafka;

import com.pengrad.telegrambot.request.SendMessage;
import org.example.controller.TelegramBotController;
import org.example.kafka.message.KafkaMessage;
import org.example.postgres.entity.TradeDecisionEntity;
import org.example.postgres.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {
    private final UserRepository repository;
    private final TelegramBotController bot;
    @Autowired
    public KafkaConsumer(TelegramBotController bot,UserRepository repository){
        this.bot = bot;
        this.repository = repository;
    }
    @KafkaListener(topics = "${kafka.topic.name}")
    public void listen(TradeDecisionEntity tradeDecision) {
        try {
            var users = repository.getUsers(tradeDecision.getTicker());
            String text = "Акция с тикером:  " +tradeDecision.getTicker()
                    + "\nЦена:" + tradeDecision.getPrice()
                    + "\nTP:" + tradeDecision.getTakeProfit()
                    + "\nSL:" + tradeDecision.getStopLoss()
                    + "\nНаправление:" + tradeDecision.getDirection();
            System.out.println(text);
            for(var user : users){
                bot.sendNotification(new SendMessage(user,text));
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
