package com.example.controller;

import com.example.configuration.BotConfig;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TelegramBotController{

    private final TelegramBot bot;
    private final CommandHandler handler;

    @Autowired
    public TelegramBotController(
            BotConfig botConfig,
            CommandHandler handler
    ) {
        this.bot = new TelegramBot(botConfig.getTelegramToken());
        this.handler = handler;
    }

    @PostConstruct
    public void run() {
        bot.setUpdatesListener(updates -> {
                    updates.forEach(update -> {
                        bot.execute(handler.handle(update));
                    });
                    return UpdatesListener.CONFIRMED_UPDATES_ALL;
                }
        );
    }

    public void sendUpdate(SendMessage message) {
        bot.execute(message);
    }

}