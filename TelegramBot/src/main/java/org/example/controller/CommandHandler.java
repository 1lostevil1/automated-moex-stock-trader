package org.example.controller;

import org.example.command.*;
import com.pengrad.telegrambot.model.Update;
import org.example.postgres.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.pengrad.telegrambot.request.SendMessage;

import java.util.Map;

@Component
public class CommandHandler {
    private static final String WRONG_COMMAND = "неверная команда!";
    private UserRepository repository;
    private Map<String, Command> commands;

    @Autowired
    public CommandHandler(UserRepository repository) {
        this.repository = repository;
        this.commands = Map.of(
                "/start", new Start(),
                "/stop", new Stop()
        );
    }

    public SendMessage handle(Update update) {
        Long id = update.message().chat().id();
        return executeCommand(update);
    }

    public SendMessage executeCommand(Update update) {
        Long id = update.message().chat().id();
        String message = update.message().text();
        Command command = null;
        if (message != null) {
            command = commands.get(message);
        }
        if (command != null) {
            return command.apply(update,repository);
        }
        return new SendMessage(id, WRONG_COMMAND);
    }

}
