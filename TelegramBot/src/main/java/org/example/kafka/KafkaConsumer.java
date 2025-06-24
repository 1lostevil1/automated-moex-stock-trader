package org.example.kafka;

import com.pengrad.telegrambot.request.SendMessage;
import org.example.controller.TelegramBotController;
import org.example.kafka.message.KafkaMessage;
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
    public void listen(KafkaMessage message) {
        try {
            var users = repository.getUsers(message.getTicker());
            String text = "Акция с тикером: " + message.getTicker()
                    + "\nЦена:" + message.getPrice()
                    + "\nКоличество:" + message.getAmount()
                    + "\nНаправление:" + message.getDirection();
            for(var user : users){
                bot.sendNotification(new SendMessage(user,text));
            }
        }
        catch (Exception e){
            System.out.println("Ошибка отправки");
        }
    }
}
